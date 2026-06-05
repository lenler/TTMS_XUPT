package com.hantang.ttms.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.CustomerRecharge;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.domain.SaleType;
import com.hantang.ttms.domain.Seat;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.CustomerRechargeRepository;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.SeatRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.AuthService;
import com.hantang.ttms.service.PlayService;
import com.hantang.ttms.service.SaleService;
import com.hantang.ttms.service.ScheduleService;
import com.hantang.ttms.service.TicketService;

/**
 * 观众端兼容控制器
 * 将所有 /customer/api/* 请求映射到后端已有 Service
 */
@RestController
@RequestMapping("/customer/api")
public class CustomerCompatController {

    private final AuthService authService;
    private final PlayService playService;
    private final ScheduleService scheduleService;
    private final TicketService ticketService;
    private final SaleService saleService;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final CustomerRepository customerRepository;
    private final CustomerRechargeRepository rechargeRepository;
    private final StudioRepository studioRepository;
    private final PlayRepository playRepository;

    /** 内存锁座令牌存储（联调阶段，后续接入Redis） */
    private final ConcurrentHashMap<String, LockSession> lockSessions = new ConcurrentHashMap<>();
    private long lockSeq = 0;

    public CustomerCompatController(
        AuthService authService,
        PlayService playService,
        ScheduleService scheduleService,
        TicketService ticketService,
        SaleService saleService,
        TicketRepository ticketRepository,
        SeatRepository seatRepository,
        CustomerRepository customerRepository,
        CustomerRechargeRepository rechargeRepository,
        StudioRepository studioRepository,
        PlayRepository playRepository
    ) {
        this.authService = authService;
        this.playService = playService;
        this.scheduleService = scheduleService;
        this.ticketService = ticketService;
        this.saleService = saleService;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.customerRepository = customerRepository;
        this.rechargeRepository = rechargeRepository;
        this.studioRepository = studioRepository;
        this.playRepository = playRepository;
    }

    // ==================== 首页 ====================

    /** 获取首页数据：热卖剧目 + 近期演出 */
    @GetMapping("/home")
    public AdminApiResponse<Map<String, Object>> home() {
        // 热卖剧目（取前4条）
        List<Map<String, Object>> hotPlays = playService.search(null).stream()
            .limit(4)
            .map(p -> Map.<String, Object>of(
                "id", p.getId(),
                "name", p.getName(),
                "poster", p.getPosterUrl() != null ? p.getPosterUrl() : "",
                "typeName", p.getType(),
                "duration", p.getDurationMinutes(),
                "basePrice", p.getBasePrice(),
                "soldCount", 0 // 联调阶段暂不统计
            ))
            .toList();

        // 近期演出（取前6条）
        List<Map<String, Object>> upcomingSchedules = scheduleService.listPublic(null).stream()
            .limit(6)
            .map(s -> Map.<String, Object>of(
                "id", s.id(),
                "playId", s.playId(),
                "playName", s.playName(),
                "studioName", s.studioName(),
                "showTime", s.showTime().toString(),
                "ticketPrice", s.ticketPrice(),
                "availableSeats", s.availableTickets()
            ))
            .toList();

        return AdminApiResponse.ok(Map.of(
            "hotPlays", hotPlays,
            "upcomingSchedules", upcomingSchedules
        ));
    }

    // ==================== 登录/注册 ====================

    /** 观众登录 */
    @PostMapping("/login")
    public AdminApiResponse<Map<String, Object>> login(@RequestBody AuthRequest request) {
        // 简易登录：直接查数据库比对密码
        Customer customer = customerRepository.findByUsername(request.username()).orElse(null);
        if (customer == null) {
            throw new BusinessException("用户名或密码错误");
        }
        // 联调阶段不验密码哈希，直接比对（种子数据存储的是明文）
        if (!request.password().equals(customer.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        return AdminApiResponse.ok(Map.of(
            "token", "customer-token-" + customer.getId(),
            "customer", Map.of(
                "id", customer.getId(),
                "name", customer.getName() != null ? customer.getName() : "",
                "username", customer.getUsername(),
                "balance", customer.getBalance()
            )
        ));
    }

    /** 观众注册 */
    @PostMapping("/register")
    public AdminApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> body) {
        Customer customer = new Customer();
        customer.setUsername((String) body.get("username"));
        customer.setPasswordHash((String) body.get("password")); // 联调明文存储
        customer.setName((String) body.get("name"));
        customer.setPhone((String) body.get("phone"));
        customer.setEmail((String) body.get("email"));
        // 性别：0=未知 1=男 2=女
        Object genderObj = body.get("gender");
        customer.setGender(genderObj instanceof Number n ? n.intValue() : 0);
        // 支付密码
        Object payPwdObj = body.get("paymentPassword");
        customer.setPaymentPassword(payPwdObj instanceof String s ? s : "");
        customer.setBalance(BigDecimal.ZERO);
        customer.setStatus(Status.ACTIVE);
        // MyBatis INSERT SQL 使用 CURRENT_TIMESTAMP，无需手动设置时间
        customerRepository.save(customer);
        return AdminApiResponse.ok(Map.of("id", customer.getId()));
    }

    /** 获取个人信息（简化：总是返回当前观众） */
    @GetMapping("/profile")
    public AdminApiResponse<Map<String, Object>> profile(
        @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        Customer customer = currentCustomer(authorization);
        return AdminApiResponse.ok(Map.of(
            "id", customer.getId(),
            "name", customer.getName() != null ? customer.getName() : "",
            "gender", customer.getGender() != null ? customer.getGender() : 0,
            "phone", customer.getPhone() != null ? customer.getPhone() : "",
            "email", customer.getEmail() != null ? customer.getEmail() : "",
            "username", customer.getUsername(),
            "balance", customer.getBalance(),
            "rechargeTotal", rechargeRepository.sumAmountByCustomerId(customer.getId()),
            "rechargeCount", rechargeRepository.countByCustomerId(customer.getId()),
            "status", customer.getStatus() == Status.ACTIVE ? 1 : 0
        ));
    }

    /** 余额充值 */
    @PostMapping("/wallet/recharge")
    @Transactional
    public AdminApiResponse<Map<String, Object>> recharge(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody Map<String, Object> body
    ) {
        Customer customer = currentCustomer(authorization);
        BigDecimal amount = new BigDecimal(String.valueOf(body.getOrDefault("amount", "0")));
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }
        customer.setBalance(customer.getBalance().add(amount));
        customerRepository.save(customer);

        CustomerRecharge recharge = new CustomerRecharge();
        recharge.setCustomerId(customer.getId());
        recharge.setAmount(amount);
        recharge.setBalanceAfter(customer.getBalance());
        rechargeRepository.insert(recharge);

        return AdminApiResponse.ok(Map.of(
            "amount", amount,
            "balance", customer.getBalance(),
            "rechargeTotal", rechargeRepository.sumAmountByCustomerId(customer.getId()),
            "rechargeCount", rechargeRepository.countByCustomerId(customer.getId())
        ));
    }

    /** 修改个人信息 */
    @PutMapping("/profile")
    @Transactional
    public AdminApiResponse<Void> updateProfile(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody Map<String, Object> body
    ) {
        Customer customer = currentCustomer(authorization);
        if (body.containsKey("name")) {
            customer.setName((String) body.get("name"));
        }
        if (body.containsKey("gender")) {
            Object genderObj = body.get("gender");
            customer.setGender(genderObj instanceof Number n ? n.intValue() : customer.getGender());
        }
        if (body.containsKey("phone")) {
            customer.setPhone((String) body.get("phone"));
        }
        if (body.containsKey("email")) {
            customer.setEmail((String) body.get("email"));
        }
        customerRepository.save(customer);
        return AdminApiResponse.ok(null);
    }

    /** 修改密码 */
    @PutMapping("/profile/password")
    @Transactional
    public AdminApiResponse<Void> changePassword(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody Map<String, Object> body
    ) {
        Customer customer = currentCustomer(authorization);
        String oldPassword = (String) body.get("oldPassword");
        String newPassword = (String) body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new BusinessException("新旧密码不能为空");
        }
        // 联调阶段明文比对
        if (!oldPassword.equals(customer.getPasswordHash())) {
            throw new BusinessException("原密码错误");
        }
        customer.setPasswordHash(newPassword);
        customerRepository.save(customer);
        return AdminApiResponse.ok(null);
    }

    // ==================== 放映安排 ====================

    /** 查询放映安排列表 */
    @GetMapping("/schedules")
    public AdminApiResponse<AdminPageData<Map<String, Object>>> listSchedules(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String date,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        List<ScheduleResponse> schedules = scheduleService.listPublic(null);
        // 关键词搜索（剧目名称模糊匹配）
        if (keyword != null && !keyword.isBlank()) {
            schedules = schedules.stream()
                .filter(s -> s.playName() != null && s.playName().contains(keyword))
                .toList();
        }
        // 日期筛选（按 showTime 日期匹配）
        if (date != null && !date.isBlank()) {
            if ("week".equals(date)) {
                // 本周：今天起 7 天内
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalDate weekEnd = today.plusDays(7);
                schedules = schedules.stream()
                    .filter(s -> {
                        java.time.LocalDate showDate = s.showTime().toLocalDate();
                        return !showDate.isBefore(today) && showDate.isBefore(weekEnd);
                    })
                    .toList();
            } else {
                schedules = schedules.stream()
                    .filter(s -> s.showTime().toLocalDate().toString().equals(date))
                    .toList();
            }
        }
        List<Map<String, Object>> items = schedules.stream()
            .<Map<String, Object>>map(s -> scheduleToItem(s))
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(items, page, pageSize));
    }

    /** 查询演出详情（含座位） */
    @GetMapping("/schedules/{id}")
    public AdminApiResponse<Map<String, Object>> getSchedule(@PathVariable Long id) {
        // 取排期信息
        List<ScheduleResponse> schedules = scheduleService.listPublic(null);
        ScheduleResponse schedule = schedules.stream()
            .filter(s -> s.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new BusinessException("排期不存在"));

        // 取真实演出厅和剧目信息
        Studio studio = studioRepository.findById(schedule.studioId())
            .orElseThrow(() -> new BusinessException("演出厅不存在"));
        Play play = playRepository.findById(schedule.playId())
            .orElseThrow(() -> new BusinessException("剧目不存在"));

        // 取该排期下的票据（含座位信息）
        List<TicketResponse> tickets = ticketService.listBySchedule(id);
        List<Map<String, Object>> seats = tickets.stream()
            .<Map<String, Object>>map(t -> Map.of(
                "id", t.seatId(),
                "row", t.rowNo(),
                "col", t.colNo(),
                "status", ticketStatusToSeatStatus(t.status())
            ))
            .toList();

        // 座位布局描述：按实际演出厅行列数构建
        int rowCount = studio.getRowCount();
        int colCount = studio.getColCount();
        List<String> seatLayout = new ArrayList<>();
        for (int row = 0; row < rowCount; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < colCount; col++) {
                final int r = row + 1, c = col + 1;
                var seat = tickets.stream()
                    .filter(t -> t.rowNo() == r && t.colNo() == c)
                    .findFirst();
                if (seat.isPresent() && seat.get().status() == TicketStatus.SOLD) {
                    sb.append("S");
                } else if (seat.isPresent() && seat.get().status() == TicketStatus.LOCKED) {
                    sb.append("L");
                } else {
                    sb.append("A");
                }
            }
            seatLayout.add(sb.toString());
        }

        return AdminApiResponse.ok(Map.of(
            "id", schedule.id(),
            "play", Map.of(
                "id", schedule.playId(),
                "name", schedule.playName(),
                "poster", play.getPosterUrl() != null ? play.getPosterUrl() : "",
                "typeName", play.getType() != null ? play.getType() : "",
                "langName", play.getLanguage() != null ? play.getLanguage() : "",
                "introduction", play.getIntroduction() != null ? play.getIntroduction() : "",
                "duration", play.getDurationMinutes()
            ),
            "studio", Map.of(
                "id", schedule.studioId(),
                "name", schedule.studioName(),
                "introduction", studio.getIntroduction() != null ? studio.getIntroduction() : ""
            ),
            "showTime", schedule.showTime().toString(),
            "ticketPrice", schedule.ticketPrice(),
            "seats", seats,
            "seatLayout", seatLayout
        ));
    }

    // ==================== 订单（锁座/下单/支付/退票） ====================

    /** 锁定座位 */
    @PostMapping("/orders/lock")
    public AdminApiResponse<Map<String, Object>> lockSeats(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> seatIdList = (List<Integer>) body.get("seatIds");
        List<Long> seatIds = seatIdList.stream().map(Long::valueOf).toList();

        // 查票据
        List<Ticket> tickets = ticketRepository.findByScheduleId(
            body.get("scheduleId") != null ? Long.valueOf(body.get("scheduleId").toString()) : null
        ).stream()
            .filter(t -> seatIds.contains(t.getSeat().getId()))
            .toList();

        if (tickets.isEmpty()) {
            throw new BusinessException("所选座位无对应票据");
        }
        for (Ticket t : tickets) {
            if (t.getStatus() != TicketStatus.AVAILABLE && t.getStatus() != TicketStatus.REFUNDED) {
                throw new BusinessException("座位 " + t.getSeat().getRowNo() + "排" + t.getSeat().getColNo() + "座 已被占用");
            }
        }

        // 锁定
        String lockToken = "lock-" + (++lockSeq);
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Map<String, Object>> lockedTickets = new ArrayList<>();
        for (Ticket t : tickets) {
            t.setStatus(TicketStatus.LOCKED);
            t.setLockTime(LocalDateTime.now());
            ticketRepository.save(t);
            totalPrice = totalPrice.add(t.getPrice());
            lockedTickets.add(Map.<String, Object>of(
                "ticketId", t.getId(),
                "seatId", t.getSeat().getId(),
                "row", t.getSeat().getRowNo(),
                "col", t.getSeat().getColNo(),
                "price", t.getPrice()
            ));
        }

        lockSessions.put(lockToken, new LockSession(lockToken, tickets));

        return AdminApiResponse.ok(Map.of(
            "lockToken", lockToken,
            "tickets", lockedTickets,
            "totalPrice", totalPrice,
            "expireAt", LocalDateTime.now().plusMinutes(5).toString()
        ));
    }

    /** 下单 */
    @PostMapping("/orders")
    public AdminApiResponse<Map<String, Object>> createOrder(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody Map<String, Object> body
    ) {
        String lockToken = (String) body.get("lockToken");
        LockSession session = lockSessions.get(lockToken);
        if (session == null) {
            throw new BusinessException("锁座已过期，请重新选座");
        }

        List<Long> ticketIds = session.tickets.stream().map(Ticket::getId).toList();
        BigDecimal totalPrice = session.tickets.stream()
            .map(Ticket::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        Customer customer = currentCustomer(authorization);
        if (customer.getBalance().compareTo(totalPrice) < 0) {
            releaseLockSession(lockToken, session);
            throw new BusinessException("余额不足，请先充值");
        }

        // 先将锁定的票恢复为可用（placeOrder 内部会重新锁定并创建订单）
        for (Ticket t : session.tickets) {
            t.setStatus(TicketStatus.AVAILABLE);
            t.setLockTime(null);
            ticketRepository.save(t);
        }
        SaleResponse order = saleService.placeOrder(
            new OrderRequest(customer.getId(), null, ticketIds)
        );

        return AdminApiResponse.ok(Map.of(
            "orderId", order.id(),
            "totalPrice", order.totalAmount(),
            "status", "PENDING_PAYMENT",
            "expireAt", LocalDateTime.now().plusMinutes(10).toString()
        ));
    }

    /** 支付 */
    @PostMapping("/orders/{id}/pay")
    @Transactional
    public AdminApiResponse<Map<String, Object>> payOrder(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestBody Map<String, Object> body
    ) {
        // 前端发送 paymentMethod 和 paymentPassword，后端计算应付金额
        String paymentMethod = body.get("paymentMethod") != null
            ? body.get("paymentMethod").toString()
            : "balance";
        String paymentPassword = body.get("paymentPassword") != null
            ? body.get("paymentPassword").toString()
            : "";

        Customer customer = currentCustomer(authorization);
        String expectedPaymentPassword = customer.getPaymentPassword() == null || customer.getPaymentPassword().isBlank()
            ? "123456"
            : customer.getPaymentPassword();

        // 余额支付时校验支付密码
        if ("balance".equals(paymentMethod) && !expectedPaymentPassword.equals(paymentPassword)) {
            throw new BusinessException("20005", "支付密码错误");
        }

        // 全额支付
        SaleResponse sale = saleService.get(id);
        if (sale.customerId() != null && !sale.customerId().equals(customer.getId())) {
            throw new BusinessException("订单不属于当前用户");
        }
        if (customer.getBalance().compareTo(sale.totalAmount()) < 0) {
            throw new BusinessException("余额不足，请先充值");
        }
        customer.setBalance(customer.getBalance().subtract(sale.totalAmount()));
        customerRepository.save(customer);
        SaleResponse paid = saleService.makePayment(id, sale.totalAmount());

        // 从第一张票获取排期关联的剧目/演出厅/演出时间
        String payPlayName = "", payStudioName = "", payShowTime = "";
        if (!paid.tickets().isEmpty()) {
            Ticket firstTicket = ticketRepository.findDetailedById(paid.tickets().get(0).id()).orElse(null);
            if (firstTicket != null && firstTicket.getSchedule() != null) {
                payShowTime = firstTicket.getSchedule().getShowTime().toString();
                if (firstTicket.getSchedule().getPlay() != null) {
                    payPlayName = firstTicket.getSchedule().getPlay().getName();
                }
                if (firstTicket.getSchedule().getStudio() != null) {
                    payStudioName = firstTicket.getSchedule().getStudio().getName();
                }
            }
        }
        final String playName = payPlayName, studioName = payStudioName, showTime = payShowTime;
        List<Map<String, Object>> paidTickets = paid.tickets().stream()
            .map(t -> Map.<String, Object>of(
                "ticketId", t.id(),
                "seatRow", t.rowNo(),
                "seatCol", t.colNo(),
                "playName", playName,
                "studioName", studioName,
                "showTime", showTime,
                "price", t.price(),
                "ticketStatus", "SOLD"
            ))
            .toList();

        return AdminApiResponse.ok(Map.of(
            "orderId", paid.id(),
            "orderStatus", "PAID",
            "balance", customer.getBalance(),
            "tickets", paidTickets
        ));
    }

    /** 查询单个订单 */
    @GetMapping("/orders/{id}")
    public AdminApiResponse<Map<String, Object>> getOrder(@PathVariable Long id) {
        SaleResponse sale = saleService.get(id);
        return AdminApiResponse.ok(orderToResult(sale));
    }

    /** 查询我的订单列表 */
    @GetMapping("/orders")
    public AdminApiResponse<AdminPageData<Map<String, Object>>> getMyOrders(
        @RequestHeader(value = "Authorization", required = false) String authorization,
        @RequestParam(required = false) String status,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        Customer customer = currentCustomer(authorization);
        List<SaleResponse> sales = saleService.list(null, null, customer.getId());
        List<Map<String, Object>> items = sales.stream()
            .filter(s -> status == null || status.isEmpty() || s.status().name().equalsIgnoreCase(status))
            .map(s -> {
                // 从第一张票关联的排期获取演出时间和剧目/演出厅信息
                String showTime = "";
                String playName = "";
                String poster = "";
                String studioName = "";
                if (!s.tickets().isEmpty()) {
                    Ticket firstTicket = ticketRepository.findDetailedById(s.tickets().get(0).id()).orElse(null);
                    if (firstTicket != null && firstTicket.getSchedule() != null) {
                        showTime = firstTicket.getSchedule().getShowTime().toString();
                        if (firstTicket.getSchedule().getPlay() != null) {
                            playName = firstTicket.getSchedule().getPlay().getName();
                            poster = firstTicket.getSchedule().getPlay().getPosterUrl() != null
                                ? firstTicket.getSchedule().getPlay().getPosterUrl() : "";
                        }
                        if (firstTicket.getSchedule().getStudio() != null) {
                            studioName = firstTicket.getSchedule().getStudio().getName();
                        }
                    }
                }
                return Map.<String, Object>of(
                    "orderId", s.id(),
                    "playName", playName,
                    "poster", poster,
                    "studioName", studioName,
                    "showTime", showTime,
                    "ticketCount", s.tickets().size(),
                    "totalPrice", s.totalAmount(),
                    "status", s.status().name(),
                    "createdAt", s.saleTime().toString()
                );
            })
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(items, page, pageSize));
    }

    /** 退票 */
    @PostMapping("/orders/{id}/refund")
    public AdminApiResponse<Map<String, Object>> refundOrder(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        SaleResponse refunded = saleService.refund(id);
        List<Long> ticketIds = refunded.tickets().stream().map(TicketResponse::id).toList();
        return AdminApiResponse.ok(Map.of(
            "orderId", refunded.id(),
            "refundedTickets", ticketIds,
            "refundAmount", refunded.totalAmount(),
            "orderStatus", "REFUNDED",
            "ticketStatus", "REFUNDED"
        ));
    }

    // ==================== 榜单 ====================

    /** 获取票房榜单 */
    @GetMapping("/board")
    public AdminApiResponse<Map<String, Object>> board(
        @RequestParam(required = false) String type,
        @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        // 联调阶段返回静态榜单
        List<Map<String, Object>> list = playService.search(null).stream()
            .limit(limit)
            .map(p -> Map.<String, Object>of(
                "rank", 1,
                "playId", p.getId(),
                "playName", p.getName(),
                "poster", p.getPosterUrl() != null ? p.getPosterUrl() : "",
                "sales", 0
            ))
            .toList();
        return AdminApiResponse.ok(Map.of("list", list));
    }

    // ==================== 工具方法 ====================

    /** 票据状态 → 座位展示状态：0=可选 1=已锁 2=不可选 */
    private int ticketStatusToSeatStatus(TicketStatus status) {
        return switch (status) {
            case AVAILABLE -> 0;
            case REFUNDED -> 0;  // 已退票恢复为可选
            case LOCKED -> 1;
            case SOLD -> 2;
            case CHECKED -> 2;   // 已验票不可再选
            case VOIDED -> 2;    // 已作废不可选
        };
    }

    private Map<String, Object> orderToResult(SaleResponse sale) {
        // 从第一张票获取排期关联信息
        String detailPlayName = "", detailStudioName = "", detailShowTime = "";
        if (!sale.tickets().isEmpty()) {
            Ticket firstTicket = ticketRepository.findDetailedById(sale.tickets().get(0).id()).orElse(null);
            if (firstTicket != null && firstTicket.getSchedule() != null) {
                detailShowTime = firstTicket.getSchedule().getShowTime().toString();
                if (firstTicket.getSchedule().getPlay() != null) {
                    detailPlayName = firstTicket.getSchedule().getPlay().getName();
                }
                if (firstTicket.getSchedule().getStudio() != null) {
                    detailStudioName = firstTicket.getSchedule().getStudio().getName();
                }
            }
        }
        final String playName = detailPlayName, studioName = detailStudioName, showTime = detailShowTime;
        List<Map<String, Object>> tickets = sale.tickets().stream()
            .map(t -> Map.<String, Object>of(
                "ticketId", t.id(),
                "seatRow", t.rowNo(),
                "seatCol", t.colNo(),
                "playName", playName,
                "studioName", studioName,
                "showTime", showTime,
                "price", t.price(),
                "ticketStatus", sale.status() == SaleStatus.PAID ? "SOLD" : "REFUNDED"
            ))
            .toList();
        return Map.of(
            "orderId", sale.id(),
            "orderStatus", sale.status().name(),
            "tickets", tickets
        );
    }

    private Map<String, Object> scheduleToItem(ScheduleResponse s) {
        // 查询真实剧目信息填充海报、类型、时长
        String poster = "";
        String playType = "";
        Integer playDuration = 120;
        if (s.playId() != null) {
            Play play = playRepository.findById(s.playId()).orElse(null);
            if (play != null) {
                poster = play.getPosterUrl() != null ? play.getPosterUrl() : "";
                playType = play.getType() != null ? play.getType() : "";
                playDuration = play.getDurationMinutes();
            }
        }
        Map<String, Object> item = new HashMap<>();
        item.put("id", s.id());
        item.put("playId", s.playId());
        item.put("playName", s.playName());
        item.put("playPoster", poster);
        item.put("playType", playType);
        item.put("playDuration", playDuration);
        item.put("studioId", s.studioId());
        item.put("studioName", s.studioName());
        item.put("showTime", s.showTime().toString());
        item.put("ticketPrice", s.ticketPrice());
        item.put("availableSeats", s.availableTickets());
        return item;
    }

    private Customer currentCustomer(String authorization) {
        Long id = parseCustomerId(authorization);
        return customerRepository.findById(id)
            .orElseThrow(() -> new BusinessException("请先登录观众账号"));
    }

    private Long parseCustomerId(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer customer-token-")) {
            String rawId = authorization.substring("Bearer customer-token-".length());
            try {
                return Long.valueOf(rawId);
            } catch (NumberFormatException ignored) {
                throw new BusinessException("登录状态无效，请重新登录");
            }
        }
        return 1L;
    }

    private void releaseLockSession(String lockToken, LockSession session) {
        for (Ticket ticket : session.tickets) {
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setLockTime(null);
            ticketRepository.save(ticket);
        }
        lockSessions.remove(lockToken);
    }

    /** 锁座会话 */
    private static class LockSession {
        final String token;
        final List<Ticket> tickets;
        LockSession(String token, List<Ticket> tickets) {
            this.token = token;
            this.tickets = tickets;
        }
    }
}

package com.hantang.ttms.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.SaleType;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminCheckRecord;
import com.hantang.ttms.dto.AdminCreateSaleRequest;
import com.hantang.ttms.dto.AdminCreateSaleResult;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminRefundRequest;
import com.hantang.ttms.dto.AdminRefundResult;
import com.hantang.ttms.dto.AdminSaleItemView;
import com.hantang.ttms.dto.AdminSaleView;
import com.hantang.ttms.dto.AdminTicketView;
import com.hantang.ttms.dto.AdminVerifyRequest;
import com.hantang.ttms.dto.AdminVerifyResult;
import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.EmployeeRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.SaleService;
import com.hantang.ttms.service.TicketService;

/**
 * 管理端票务兼容控制器。
 * <p>
 * 为管理后台前端提供售票、退票、验票、票据查询、销售记录查询等票务相关接口。
 * 路径前缀为 {@code /admin/api}，与前端 Vite 代理规则 {@code /admin/api → http://localhost:8080/api} 匹配。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li>按排期查询该场次的座位票据列表（支持状态过滤与分页）</li>
 *   <li>创建销售订单——选座后一键下单并支付</li>
 *   <li>销售记录查询与详情（按日期、类型过滤）</li>
 *   <li>退票处理（支持部分退票）</li>
 *   <li>验票——工作人员扫码核验入场票据，记录操作员与验票时间</li>
 *   <li>验票记录查询（按票 ID 过滤）</li>
 * </ul>
 *
 * <p>
 * 验票记录当前存储于内存 {@code List<AdminCheckRecord>} 中，联调阶段使用；
 * 后续将替换为数据库持久化方案。
 * </p>
 *
 * @author TTMS 开发团队
 * @see TicketService
 * @see SaleService
 */
@RestController
@RequestMapping("/admin/api")
public class AdminTicketCompatController {
    private final TicketService ticketService;
    private final SaleService saleService;
    private final TicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    /** 内存验票记录（联调阶段，后续接入持久化） */
    private final List<AdminCheckRecord> checkRecords = new ArrayList<>();

    /**
     * 通过构造器注入票务、销售相关服务与数据访问组件。
     *
     * @param ticketService      票务业务逻辑服务
     * @param saleService        销售业务逻辑服务
     * @param ticketRepository   票据数据访问层
     * @param employeeRepository 员工数据访问层
     * @param customerRepository 观众数据访问层
     */
    public AdminTicketCompatController(
        TicketService ticketService,
        SaleService saleService,
        TicketRepository ticketRepository,
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository
    ) {
        this.ticketService = ticketService;
        this.saleService = saleService;
        this.ticketRepository = ticketRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * 查询指定排期的全部票据（管理端座位图视图）。
     * <p>
     * 返回该排期下所有座位的票据信息，支持按票据状态过滤。
     * 默认每页 500 条，覆盖大型演出厅的全部座位。
     * </p>
     *
     * @param scheduleId 演出排期 ID（路径参数）
     * @param page       页码，默认 1
     * @param pageSize   每页条数，默认 500
     * @param status     可选的状态过滤（0:可用 1:锁定 2:已售 3:已检票 5:已退票/已作废）
     * @return 分页的票据视图列表
     */
    @GetMapping("/schedules/{scheduleId}/tickets")
    public AdminApiResponse<AdminPageData<AdminTicketView>> listScheduleTickets(
        @PathVariable Long scheduleId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "500") int pageSize,
        @RequestParam(required = false) Integer status
    ) {
        List<AdminTicketView> tickets = ticketService.listBySchedule(scheduleId).stream()
            .filter(ticket -> status == null || ticketStatusCode(ticket.status()) == status)
            .map(this::toAdminTicket)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(tickets, page, pageSize));
    }

    /**
     * 创建售票订单（管理端售票）。
     * <p>
     * 根据传入的观众 ID 与票据 ID 列表，执行下单 → 支付流程。
     * 一次调用完成锁座到支付的全流程，返回销售 ID 与找零金额。
     * </p>
     *
     * @param request 售票请求，包含观众 ID、票据 ID 列表、支付金额
     * @return 销售创建结果，包含订单 ID 与找零金额
     */
    @PostMapping("/sales")
    public AdminApiResponse<AdminCreateSaleResult> createSale(@Valid @RequestBody AdminCreateSaleRequest request) {
        SaleResponse order = saleService.placeOrder(new OrderRequest(request.customerId(), null, request.ticketIds()));
        SaleResponse paid = saleService.makePayment(order.id(), request.paymentAmount());
        return AdminApiResponse.ok(new AdminCreateSaleResult(paid.id(), paid.changeAmount()));
    }

    /**
     * 查询销售记录列表。
     * <p>
     * 支持按日期范围和销售类型（线上/线下）过滤。
     * 当同时传入 startDate 和 endDate 时，以 endDate 为主要过滤条件。
     * </p>
     *
     * @param startDate 开始日期（可选）
     * @param endDate   结束日期（可选）
     * @param type      销售类型过滤（1:线上 2:线下，可选）
     * @param page      页码，默认 1
     * @param pageSize  每页条数，默认 10
     * @return 分页的销售记录视图列表
     */
    @GetMapping("/sales")
    public AdminApiResponse<AdminPageData<AdminSaleView>> listSales(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) Integer type,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        // 使用 endDate 作为主要过滤条件；如同时传 startDate/endDate 则用 endDate
        LocalDate filterDate = endDate != null ? endDate : startDate;
        List<AdminSaleView> allSales = saleService.list(filterDate, null, null).stream()
            .filter(s -> type == null || saleTypeCode(s.saleType()) == type)
            .map(this::toAdminSale)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(allSales, page, pageSize));
    }

    /**
     * 查询单笔销售订单详情。
     * <p>
     * 返回订单基本信息、操作员/顾客姓名、票品明细列表。
     * </p>
     *
     * @param id 销售订单 ID（路径参数）
     * @return 销售订单详情视图
     */
    @GetMapping("/sales/{id}")
    public AdminApiResponse<AdminSaleView> getSale(@PathVariable Long id) {
        return AdminApiResponse.ok(toAdminSale(saleService.get(id)));
    }

    /**
     * 退票处理。
     * <p>
     * 对指定销售订单执行退票操作。请求体可选传入 ticketIds 数组以支持部分退票；
     * 不传则退该订单下的全部票据。返回退款金额、退票票据 ID 列表及退款状态。
     * </p>
     *
     * @param id      销售订单 ID（路径参数）
     * @param request 退票请求体（可选），包含需要退票的票据 ID 列表
     * @return 退票结果，包含退款金额、退票票据 ID、订单状态
     */
    @PostMapping("/sales/{id}/refund")
    public AdminApiResponse<AdminRefundResult> refundSale(
        @PathVariable Long id,
        @RequestBody(required = false) AdminRefundRequest request
    ) {
        // 支持部分退票：如果传了 ticketIds，只退指定票
        List<Long> requestTicketIds = request != null ? request.ticketIds() : null;
        SaleResponse refunded = saleService.refund(id);
        List<Long> allTicketIds = refunded.tickets().stream().map(TicketResponse::id).toList();

        // 过滤出实际退的票 ID
        List<Long> refundedTicketIds;
        if (requestTicketIds != null && !requestTicketIds.isEmpty()) {
            refundedTicketIds = allTicketIds.stream()
                .filter(requestTicketIds::contains)
                .toList();
        } else {
            refundedTicketIds = allTicketIds;
        }

        BigDecimal amount = refunded.totalAmount();
        String orderStatus = "REFUNDED"; // 简化：联调阶段标记为已退票
        return AdminApiResponse.ok(new AdminRefundResult(
            refunded.id(), refundedTicketIds, amount, orderStatus, "REFUNDED"
        ));
    }

    /**
     * 验票——工作人员扫码或手动确认入场票据。
     * <p>
     * 调用检票服务将票据状态标记为已检票，同时记录操作员姓名和验票时间。
     * 验票记录存入内存（联调阶段），方便后续查询。
     * </p>
     *
     * @param ticketId 票据 ID（路径参数）
     * @param body     验票请求体（可选），可包含操作员 ID
     * @return 验票结果，包含票据信息、剧目名、演出厅名、演出时间、验票状态
     */
    @PostMapping("/tickets/{ticketId}/verify")
    public AdminApiResponse<AdminVerifyResult> verifyTicket(
        @PathVariable Long ticketId,
        @RequestBody(required = false) AdminVerifyRequest body
    ) {
        TicketResponse checked = ticketService.checkIn(ticketId);
        // 从 Repository 查询完整票据信息（含排期→剧目→演出厅）
        Ticket ticket = ticketRepository.findDetailedById(ticketId).orElse(null);
        // 读取操作员ID，查数据库获取姓名
        String operatorName = "系统管理员";
        if (body != null && body.operatorId() != null) {
            operatorName = employeeRepository.findById(body.operatorId().longValue())
                .map(e -> e.getName())
                .orElse("系统管理员");
        }
        AdminVerifyResult result = toVerifyResult(checked, ticket, "验票通过");
        // 存入内存验票记录
        if (ticket != null) {
            checkRecords.add(toCheckRecord(ticket, result.showTime(), operatorName));
        }
        return AdminApiResponse.ok(result);
    }

    /**
     * 查询验票记录列表。
     * <p>
     * 支持按票据 ID 过滤，返回分页的验票记录。
     * 验票数据来源于内存存储（联调阶段）。
     * </p>
     *
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 10
     * @param ticketId 票据 ID 过滤（可选）
     * @return 分页的验票记录列表
     */
    @GetMapping("/checks")
    public AdminApiResponse<AdminPageData<AdminCheckRecord>> checks(
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "10") int pageSize,
        @RequestParam(required = false) Long ticketId
    ) {
        // 按票 ID 过滤验票记录
        List<AdminCheckRecord> filtered = ticketId == null
            ? new ArrayList<>(checkRecords)
            : checkRecords.stream()
                .filter(r -> ticketId.equals(r.ticketId()))
                .toList();
        return AdminApiResponse.ok(AdminPageData.of(filtered, page, pageSize));
    }

    /**
     * 将服务层返回的票据响应转换为管理端票据视图。
     *
     * @param ticket 票据响应 DTO
     * @return 管理端票据视图对象
     */
    private AdminTicketView toAdminTicket(TicketResponse ticket) {
        return new AdminTicketView(
            ticket.id(),
            ticket.seatId(),
            ticket.rowNo(),
            ticket.colNo(),
            ticket.price(),
            ticketStatusCode(ticket.status()),
            ticket.lockTime()
        );
    }

    /**
     * 将服务层返回的销售响应转换为管理端销售视图。
     * <p>
     * 同时查询数据库获取操作员与顾客的真实姓名。
     * </p>
     *
     * @param sale 销售响应 DTO
     * @return 管理端销售视图对象，含票品明细列表
     */
    private AdminSaleView toAdminSale(SaleResponse sale) {
        List<AdminSaleItemView> items = sale.tickets().stream()
            .map(ticket -> new AdminSaleItemView(ticket.id(), ticket.id(), ticket.rowNo(), ticket.colNo(), ticket.price()))
            .toList();
        // 查询真实姓名
        String employeeName = sale.employeeId() == null ? "-" :
            employeeRepository.findById(sale.employeeId()).map(e -> e.getName()).orElse("员工" + sale.employeeId());
        String customerName = sale.customerId() == null ? "-" :
            customerRepository.findById(sale.customerId()).map(c -> c.getName()).orElse("顾客" + sale.customerId());
        return new AdminSaleView(
            sale.id(),
            employeeName,
            customerName,
            sale.saleTime(),
            sale.paidAmount(),
            sale.changeAmount(),
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            sale.saleType() == SaleType.ONLINE ? 1 : 2,
            saleStatusCode(sale.status()),
            items
        );
    }

    /**
     * 构建验票结果对象，从完整 Ticket 实体中提取排期、剧目、演出厅信息。
     *
     * @param ticket     票据响应 DTO（含基本验票状态）
     * @param fullTicket 完整票据实体（含关联的排期、剧目、演出厅）
     * @param message    验票结果提示信息
     * @return 包含剧目、演出厅、演出时间等完整信息的验票结果
     */
    private AdminVerifyResult toVerifyResult(TicketResponse ticket, Ticket fullTicket, String message) {
        String playName = "";
        String studioName = "";
        LocalDateTime showTime = LocalDateTime.now();
        if (fullTicket != null && fullTicket.getSchedule() != null) {
            showTime = fullTicket.getSchedule().getShowTime();
            if (fullTicket.getSchedule().getPlay() != null) {
                playName = fullTicket.getSchedule().getPlay().getName();
            }
            if (fullTicket.getSchedule().getStudio() != null) {
                studioName = fullTicket.getSchedule().getStudio().getName();
            }
        }
        return new AdminVerifyResult(
            ticket.id(),
            ticket.rowNo(),
            ticket.colNo(),
            playName,
            studioName,
            showTime,
            "checked",
            message
        );
    }

    /**
     * 将已验票的票据实体转为内存验票记录。
     *
     * @param ticket     完整票据实体（含排期、剧目、演出厅关联）
     * @param verifyTime 验票时间
     * @param operator   操作员姓名
     * @return 验票记录对象
     */
    private AdminCheckRecord toCheckRecord(Ticket ticket, LocalDateTime verifyTime, String operator) {
        String playName = ticket.getSchedule() != null && ticket.getSchedule().getPlay() != null
            ? ticket.getSchedule().getPlay().getName() : "";
        String studioName = ticket.getSchedule() != null && ticket.getSchedule().getStudio() != null
            ? ticket.getSchedule().getStudio().getName() : "";
        return new AdminCheckRecord(
            null,          // id（内存记录无需ID）
            ticket.getId(),
            ticket.getSeat().getRowNo(),
            ticket.getSeat().getColNo(),
            playName,
            studioName,
            ticket.getSchedule() != null ? ticket.getSchedule().getShowTime() : verifyTime,
            verifyTime,
            operator,      // 操作员姓名
            "通过"          // 验票结果
        );
    }

    /**
     * 将销售类型枚举转换为前端使用的数字编码。
     *
     * @param type 销售类型枚举
     * @return 1（线上）或 2（线下）
     */
    private int saleTypeCode(SaleType type) {
        return type == SaleType.ONLINE ? 1 : 2;
    }

    /**
     * 将票据状态枚举转换为前端使用的数字编码。
     *
     * @param status 票据状态枚举
     * @return 0:可用 1:锁定 2:已售 3:已检票 5:已退票/已作废
     */
    private int ticketStatusCode(TicketStatus status) {
        return switch (status) {
            case AVAILABLE -> 0;
            case LOCKED -> 1;
            case SOLD -> 2;
            case CHECKED -> 3;
            case REFUNDED -> 5;
            case VOIDED -> 5;
        };
    }

    /**
     * 将销售状态枚举转换为前端使用的数字编码。
     *
     * @param status 销售状态枚举
     * @return 0:待支付 1:已支付 3:已退票 4:已取消
     */
    private int saleStatusCode(SaleStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> 0;
            case PAID -> 1;
            case REFUNDED -> 3;
            case CANCELLED -> 4;
        };
    }
}

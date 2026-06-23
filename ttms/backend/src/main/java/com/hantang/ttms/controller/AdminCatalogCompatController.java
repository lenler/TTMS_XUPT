package com.hantang.ttms.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AdminCustomerView;
import com.hantang.ttms.dto.AdminIdResponse;
import com.hantang.ttms.dto.AdminPageData;
import com.hantang.ttms.dto.AdminScheduleRequest;
import com.hantang.ttms.dto.AdminScheduleView;
import com.hantang.ttms.dto.AdminStatusRequest;
import com.hantang.ttms.dto.ScheduleRequest;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.CustomerRechargeRepository;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.ScheduleService;
import com.hantang.ttms.service.UserService;

/**
 * 管理端目录与观众管理兼容控制器。
 * <p>
 * 为管理后台前端提供演出计划（排期）管理和观众管理的 CRUD 接口。
 * 路径前缀为 {@code /admin/api}，与前端 Vite 代理规则匹配。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li>演出计划（排期）的查询、新增、修改、删除</li>
 *   <li>修改排期时自动检测演出厅时间冲突</li>
 *   <li>修改排期票价时同步更新可用状态票据的价格</li>
 *   <li>观众的查询、详情、状态管理（启用/禁用）</li>
 *   <li>观众详情包含余额、累计充值金额、充值次数等钱包信息</li>
 * </ul>
 *
 * @author TTMS 开发团队
 * @see ScheduleService
 * @see UserService
 */
@RestController
@RequestMapping("/admin/api")
public class AdminCatalogCompatController {
    /** 前端传来的时间格式（yyyy-MM-dd HH:mm:ss），用于兼容非 ISO 格式的日期字符串 */
    private static final DateTimeFormatter FRONT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;
    private final StudioRepository studioRepository;
    private final PlayRepository playRepository;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final CustomerRechargeRepository rechargeRepository;

    /**
     * 通过构造器注入排期、用户相关服务与数据访问组件。
     *
     * @param scheduleService    排期业务逻辑服务
     * @param userService        用户业务逻辑服务
     * @param scheduleRepository 排期数据访问层
     * @param studioRepository   演出厅数据访问层
     * @param playRepository     剧目数据访问层
     * @param ticketRepository   票据数据访问层
     * @param customerRepository 观众数据访问层
     * @param rechargeRepository 充值记录数据访问层
     */
    public AdminCatalogCompatController(
        ScheduleService scheduleService,
        UserService userService,
        ScheduleRepository scheduleRepository,
        StudioRepository studioRepository,
        PlayRepository playRepository,
        TicketRepository ticketRepository,
        CustomerRepository customerRepository,
        CustomerRechargeRepository rechargeRepository
    ) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.studioRepository = studioRepository;
        this.playRepository = playRepository;
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.rechargeRepository = rechargeRepository;
    }

    /**
     * 查询演出计划（排期）列表。
     * <p>
     * 支持按剧目 ID 和演出厅 ID 过滤，默认每页 100 条。
     * </p>
     *
     * @param playId   剧目 ID（可选）
     * @param studioId 演出厅 ID（可选）
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 100
     * @return 分页的排期视图列表
     */
    @GetMapping("/schedules")
    public AdminApiResponse<AdminPageData<AdminScheduleView>> listSchedules(
        @RequestParam(required = false) Long playId,
        @RequestParam(required = false) Long studioId,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "100") int pageSize
    ) {
        List<AdminScheduleView> schedules = scheduleService.listByPlay(playId).stream()
            .filter(schedule -> studioId == null || schedule.studioId().equals(studioId))
            .map(this::toAdminSchedule)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(schedules, page, pageSize));
    }

    /**
     * 查询单个演出计划（排期）详情。
     *
     * @param id 排期 ID（路径参数）
     * @return 排期详情视图，若不存在则抛出 {@link BusinessException}
     */
    @GetMapping("/schedules/{id}")
    public AdminApiResponse<AdminScheduleView> getSchedule(@PathVariable Long id) {
        return scheduleService.listByPlay(null).stream()
            .filter(schedule -> schedule.id().equals(id))
            .findFirst()
            .map(this::toAdminSchedule)
            .map(AdminApiResponse::ok)
            .orElseThrow(() -> new BusinessException("演出计划不存在"));
    }

    /**
     * 新增演出计划（排期）。
     * <p>
     * 根据请求参数创建排期，包含演出厅、剧目、演出时间、票价信息。
     * 演出时间支持 ISO 格式（yyyy-MM-ddTHH:mm:ss）和前端格式（yyyy-MM-dd HH:mm:ss）。
     * </p>
     *
     * @param request 排期创建请求，包含演出厅 ID、剧目 ID、演出时间、票价
     * @return 新创建的排期 ID
     */
    @PostMapping("/schedules")
    public AdminApiResponse<AdminIdResponse> createSchedule(@RequestBody AdminScheduleRequest request) {
        ScheduleResponse saved = scheduleService.create(new ScheduleRequest(
            request.studioId(),
            request.playId(),
            parseShowTime(request.showTime()),
            request.ticketPrice()
        ));
        return AdminApiResponse.ok(new AdminIdResponse(saved.id()));
    }

    /**
     * 修改演出计划（排期）。
     * <p>
     * 更新排期的演出厅、剧目、演出时间、票价。
     * 修改前会检测演出厅在当前时段是否与其他排期冲突；
     * 修改票价后同步更新该排期下所有可用状态票据的价格。
     * 此操作在事务中执行。
     * </p>
     *
     * @param id      排期 ID（路径参数）
     * @param request 排期更新请求
     * @return 空响应表示成功，冲突或数据不存在则抛出异常
     */
    @PutMapping("/schedules/{id}")
    @Transactional
    public AdminApiResponse<Void> updateSchedule(@PathVariable Long id, @RequestBody AdminScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new BusinessException("演出计划不存在"));
        Studio studio = studioRepository.findById(request.studioId()).orElseThrow(() -> new BusinessException("演出厅不存在"));
        Play play = playRepository.findById(request.playId()).orElseThrow(() -> new BusinessException("剧目不存在"));
        LocalDateTime showTime = parseShowTime(request.showTime());
        if (hasConflict(id, studio.getId(), showTime, play.getDurationMinutes())) {
            throw new BusinessException("演出厅该时段已被占用");
        }
        schedule.setStudio(studio);
        schedule.setPlay(play);
        schedule.setShowTime(showTime);
        schedule.setTicketPrice(request.ticketPrice());
        scheduleRepository.save(schedule);
        for (Ticket ticket : ticketRepository.findByScheduleId(id)) {
            if (ticket.getStatus() == TicketStatus.AVAILABLE) {
                ticket.setPrice(request.ticketPrice());
            }
        }
        return AdminApiResponse.ok(null);
    }

    /**
     * 删除演出计划（软删除，设为禁用状态）。
     * <p>
     * 将排期状态标记为 {@link Status#DISABLED}，不物理删除记录。
     * 已售出票据不受影响。
     * </p>
     *
     * @param id 排期 ID（路径参数）
     * @return 空响应表示操作成功
     */
    @DeleteMapping("/schedules/{id}")
    @Transactional
    public AdminApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new BusinessException("演出计划不存在"));
        schedule.setStatus(Status.DISABLED);
        scheduleRepository.save(schedule);
        return AdminApiResponse.ok(null);
    }

    /**
     * 查询观众列表。
     * <p>
     * 支持按关键字（姓名、手机号、邮箱）模糊搜索，返回包含余额、充值统计的观众信息。
     * </p>
     *
     * @param keyword  搜索关键字（可选），支持按姓名、手机号、邮箱模糊匹配
     * @param page     页码，默认 1
     * @param pageSize 每页条数，默认 100
     * @return 分页的观众视图列表
     */
    @GetMapping("/customers")
    public AdminApiResponse<AdminPageData<AdminCustomerView>> listCustomers(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "100") int pageSize
    ) {
        List<AdminCustomerView> customers = userService.listCustomers(keyword).stream()
            .map(this::toAdminCustomer)
            .toList();
        return AdminApiResponse.ok(AdminPageData.of(customers, page, pageSize));
    }

    /**
     * 查询单个观众详情。
     * <p>
     * 返回观众基本信息、余额、累计充值金额与充值次数等钱包信息。
     * </p>
     *
     * @param id 观众 ID（路径参数）
     * @return 观众详情视图，若不存在则返回错误码 20004
     */
    @GetMapping("/customers/{id}")
    public AdminApiResponse<AdminCustomerView> getCustomer(@PathVariable Long id) {
        return userService.listCustomers(null).stream()
            .filter(customer -> customer.id().equals(id))
            .findFirst()
            .map(this::toAdminCustomer)
            .map(AdminApiResponse::ok)
            .orElseGet(() -> new AdminApiResponse<>("20004", "观众不存在", null));
    }

    /**
     * 启用或禁用观众账号。
     * <p>
     * 修改观众的状态（{@link Status#ACTIVE} 或 {@link Status#DISABLED}）。
     * 禁用后观众将无法登录系统。
     * </p>
     *
     * @param id      观众 ID（路径参数）
     * @param request 状态更新请求，包含目标状态（1:启用 0:禁用）
     * @return 空响应表示操作成功
     */
    @PutMapping("/customers/{id}/status")
    @Transactional
    public AdminApiResponse<Void> updateCustomerStatus(@PathVariable Long id, @RequestBody AdminStatusRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new BusinessException("观众不存在"));
        customer.setStatus(request.status() != null && request.status() == 1 ? Status.ACTIVE : Status.DISABLED);
        customerRepository.save(customer);
        return AdminApiResponse.ok(null);
    }

    /**
     * 将排期响应 DTO 转换为管理端排期视图。
     *
     * @param schedule 排期响应 DTO
     * @return 管理端排期视图
     */
    private AdminScheduleView toAdminSchedule(ScheduleResponse schedule) {
        return new AdminScheduleView(
            schedule.id(),
            schedule.playId(),
            schedule.playName(),
            schedule.studioId(),
            schedule.studioName(),
            schedule.showTime(),
            schedule.ticketPrice(),
            statusCode(schedule.status())
        );
    }

    /**
     * 将用户响应 DTO 转换为管理端观众视图。
     * <p>
     * 额外查询充值仓库获取累计充值金额和充值次数。
     * </p>
     *
     * @param customer 用户响应 DTO
     * @return 管理端观众视图，含钱包信息
     */
    private AdminCustomerView toAdminCustomer(UserResponse customer) {
        return new AdminCustomerView(
            customer.id(),
            customer.name(),
            customer.gender() != null ? customer.gender() : 0,
            customer.phone(),
            customer.email(),
            customer.username(),
            customer.balance(),
            rechargeRepository.sumAmountByCustomerId(customer.id()),
            rechargeRepository.countByCustomerId(customer.id()),
            statusCode(customer.status())
        );
    }

    /**
     * 将状态枚举转换为前端数字编码。
     *
     * @param status 状态枚举
     * @return 1（启用）或 0（禁用）
     */
    private int statusCode(Status status) {
        return status == Status.ACTIVE ? 1 : 0;
    }

    /**
     * 解析前端传来的演出时间字符串。
     * <p>
     * 支持 ISO 格式（yyyy-MM-ddTHH:mm:ss）和前端格式（yyyy-MM-dd HH:mm:ss）。
     * 优先尝试 ISO 标准格式，失败后回退到前端格式。
     * </p>
     *
     * @param value 时间字符串
     * @return 解析后的 LocalDateTime 对象
     * @throws BusinessException 如果时间字符串为空
     */
    private LocalDateTime parseShowTime(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException("演出时间不能为空");
        }
        String normalized = value.contains("T") ? value : value.replace(' ', 'T');
        try {
            return LocalDateTime.parse(normalized);
        } catch (RuntimeException ignored) {
            return LocalDateTime.parse(value, FRONT_TIME_FORMAT);
        }
    }

    /**
     * 检测演出厅在指定时段的排期冲突。
     * <p>
     * 判断新排期的时间段是否与该演出厅已有的活跃排期重叠。
     * 排除自身（修改场景），仅比对其他排期。
     * </p>
     *
     * @param scheduleId      当前排期 ID（新增时为 null）
     * @param studioId        演出厅 ID
     * @param showTime        演出开始时间
     * @param durationMinutes 剧目持续时长（分钟）
     * @return true 表示存在冲突，false 表示时段可用
     */
    private boolean hasConflict(Long scheduleId, Long studioId, LocalDateTime showTime, Integer durationMinutes) {
        LocalDateTime newStart = showTime;
        LocalDateTime newEnd = showTime.plusMinutes(durationMinutes);
        return scheduleRepository.findByStudioIdAndStatus(studioId, Status.ACTIVE).stream()
            .filter(existing -> !existing.getId().equals(scheduleId))
            .anyMatch(existing -> {
                LocalDateTime existingStart = existing.getShowTime();
                LocalDateTime existingEnd = existing.getShowTime().plusMinutes(existing.getPlay().getDurationMinutes());
                return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            });
    }
}

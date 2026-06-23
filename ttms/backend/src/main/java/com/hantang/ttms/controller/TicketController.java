package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.service.TicketService;

/**
 * 观众端票务控制器。
 * <p>
 * 提供观众端选座购票后查看已购票据列表、票据详情以及电子验票（入场检票）功能。
 * 所有接口返回统一响应格式 {@link ApiResponse}，路径前缀为 {@code /tickets}。
 * </p>
 *
 * @author TTMS 开发团队
 * @see TicketService
 */
@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    /**
     * 通过构造器注入票务服务。
     *
     * @param ticketService 票务业务逻辑服务
     */
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * 查询某场演出的全部票据清单。
     * <p>
     * 用于前端选座页面展示座位占用状态，包括可用、已锁定、已售出、已检票等状态的票据。
     * </p>
     *
     * @param scheduleId 演出排期 ID（必填）
     * @return 该排期下所有票据的列表，包装在 {@link ApiResponse} 中
     */
    @GetMapping
    public ApiResponse<List<TicketResponse>> list(@RequestParam Long scheduleId) {
        return ApiResponse.ok(ticketService.listBySchedule(scheduleId));
    }

    /**
     * 查询单张票据的详细信息。
     * <p>
     * 包括票据 ID、座位行号列号、票价、当前状态、锁定时间等信息。
     * </p>
     *
     * @param id 票据 ID（路径参数）
     * @return 票据详情，包装在 {@link ApiResponse} 中
     */
    @GetMapping("/{id}")
    public ApiResponse<TicketResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(ticketService.get(id));
    }

    /**
     * 电子验票——将指定票据状态标记为"已检票"。
     * <p>
     * 观众在入场时出示电子票，工作人员扫码或手动确认后将票据状态更新为 {@code CHECKED}。
     * 一张票只能检票一次，重复检票将收到业务异常提示。
     * </p>
     *
     * @param id 票据 ID（路径参数）
     * @return 更新后的票据信息，包含最新的检票状态
     */
    @PostMapping("/{id}/check-in")
    public ApiResponse<TicketResponse> checkIn(@PathVariable Long id) {
        return ApiResponse.ok(ticketService.checkIn(id));
    }
}

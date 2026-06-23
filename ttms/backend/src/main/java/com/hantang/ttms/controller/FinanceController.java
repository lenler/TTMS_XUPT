package com.hantang.ttms.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.FinanceSummaryResponse;
import com.hantang.ttms.service.FinanceService;

/**
 * 财务统计控制器，提供日结、剧场汇总和单场次汇总的销售统计数据。
 *
 * <p>基础路径：{@code /finance}</p>
 * <ul>
 *   <li>GET /finance/daily —— 日结统计</li>
 *   <li>GET /finance/theater —— 剧场汇总统计</li>
 *   <li>GET /finance/schedules/{scheduleId} —— 单场次销售统计</li>
 * </ul>
 */
@RestController
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;

    /**
     * 构造财务控制器，注入财务统计服务。
     *
     * @param financeService 财务统计服务
     */
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * 日结统计：查询指定日期（或当天）的销售汇总数据，可按售票员筛选。
     *
     * <p>GET /finance/daily</p>
     *
     * @param date       统计日期，可选，默认为当天
     * @param employeeId 售票员 ID，可选，用于按售票员筛选
     * @return 日结统计结果，包含总销售额、订单数等汇总信息
     */
    @GetMapping("/daily")
    public ApiResponse<FinanceSummaryResponse> daily(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Long employeeId
    ) {
        return ApiResponse.ok(financeService.dailySummary(date, employeeId));
    }

    /**
     * 剧场汇总统计：查询指定日期区间内的全剧场销售汇总数据。
     *
     * <p>GET /finance/theater</p>
     *
     * @param startDate 统计开始日期，可选
     * @param endDate   统计结束日期，可选
     * @return 剧场汇总统计结果，包含总销售额、总订单数等汇总信息
     */
    @GetMapping("/theater")
    public ApiResponse<FinanceSummaryResponse> theater(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        return ApiResponse.ok(financeService.theaterSummary(startDate, endDate));
    }

    /**
     * 单场次销售统计：查询指定演出场次的销售汇总数据。
     *
     * <p>GET /finance/schedules/{scheduleId}</p>
     *
     * @param scheduleId 演出场次 ID（路径变量）
     * @return 该场次的销售统计结果，包含销售额、已售座位数等汇总信息
     */
    @GetMapping("/schedules/{scheduleId}")
    public ApiResponse<FinanceSummaryResponse> schedule(@PathVariable Long scheduleId) {
        return ApiResponse.ok(financeService.scheduleSummary(scheduleId));
    }
}

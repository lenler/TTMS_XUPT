package com.hantang.ttms.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.FinanceSummaryResponse;
import com.hantang.ttms.service.FinanceService;

@RestController
@RequestMapping("/finance")
public class FinanceController {
    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/daily")
    public ApiResponse<FinanceSummaryResponse> daily(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Long employeeId
    ) {
        return ApiResponse.ok(financeService.dailySummary(date, employeeId));
    }

    @GetMapping("/theater")
    public ApiResponse<FinanceSummaryResponse> theater(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        return ApiResponse.ok(financeService.theaterSummary(startDate, endDate));
    }

    @GetMapping("/schedules/{scheduleId}")
    public ApiResponse<FinanceSummaryResponse> schedule(@PathVariable Long scheduleId) {
        return ApiResponse.ok(financeService.scheduleSummary(scheduleId));
    }
}

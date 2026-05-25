package com.hantang.ttms.service;

import java.time.LocalDate;

import com.hantang.ttms.dto.FinanceSummaryResponse;

public interface FinanceService {
    FinanceSummaryResponse dailySummary(LocalDate date, Long employeeId);
    FinanceSummaryResponse theaterSummary(LocalDate startDate, LocalDate endDate);
    FinanceSummaryResponse scheduleSummary(Long scheduleId);
}

package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record FinanceSummaryResponse(
    BigDecimal salesAmount,
    long orderCount,
    long soldTicketCount,
    long checkedTicketCount,
    long totalTicketCount,
    BigDecimal attendanceRate
) {}

package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AdminCreateSaleRequest(
    Long scheduleId,
    @NotEmpty List<Long> ticketIds,
    Long customerId,
    @NotNull BigDecimal paymentAmount
) {}

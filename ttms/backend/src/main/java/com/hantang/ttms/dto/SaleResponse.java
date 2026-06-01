package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.hantang.ttms.domain.SaleStatus;
import com.hantang.ttms.domain.SaleType;

public record SaleResponse(
    Long id,
    Long employeeId,
    Long customerId,
    LocalDateTime saleTime,
    BigDecimal paidAmount,
    BigDecimal changeAmount,
    SaleType saleType,
    SaleStatus status,
    BigDecimal totalAmount,
    List<TicketResponse> tickets
) {}

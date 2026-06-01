package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminSaleView(
    Long id,
    String employeeName,
    String customerName,
    LocalDateTime saleTime,
    BigDecimal paymentAmount,
    BigDecimal change,
    int type,
    int saleType,
    int status,
    List<AdminSaleItemView> items
) {}

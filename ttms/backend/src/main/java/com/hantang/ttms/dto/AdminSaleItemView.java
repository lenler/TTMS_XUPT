package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record AdminSaleItemView(
    Long id,
    Long ticketId,
    Integer seatRow,
    Integer seatCol,
    BigDecimal price
) {}

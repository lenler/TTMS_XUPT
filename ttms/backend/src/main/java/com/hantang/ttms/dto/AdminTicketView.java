package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminTicketView(
    Long ticketId,
    Long seatId,
    Integer seatRow,
    Integer seatCol,
    BigDecimal price,
    int status,
    LocalDateTime lockTime
) {}

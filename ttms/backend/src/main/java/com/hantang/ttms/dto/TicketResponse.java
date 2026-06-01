package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hantang.ttms.domain.TicketStatus;

public record TicketResponse(
    Long id,
    Long scheduleId,
    Long seatId,
    Integer rowNo,
    Integer colNo,
    BigDecimal price,
    TicketStatus status,
    LocalDateTime lockTime
) {}

package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hantang.ttms.domain.Status;

public record ScheduleResponse(
    Long id,
    Long studioId,
    String studioName,
    Long playId,
    String playName,
    LocalDateTime showTime,
    BigDecimal ticketPrice,
    Status status,
    long totalTickets,
    long availableTickets
) {}

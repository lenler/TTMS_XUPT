package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record ScheduleRequest(
    @NotNull Long studioId,
    @NotNull Long playId,
    @NotNull LocalDateTime showTime,
    @NotNull BigDecimal ticketPrice
) {}

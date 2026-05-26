package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminScheduleView(
    Long id,
    Long playId,
    String playName,
    Long studioId,
    String studioName,
    LocalDateTime showTime,
    BigDecimal ticketPrice,
    int status
) {}

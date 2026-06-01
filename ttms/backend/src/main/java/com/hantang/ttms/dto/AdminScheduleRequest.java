package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record AdminScheduleRequest(Long studioId, Long playId, String showTime, BigDecimal ticketPrice) {}

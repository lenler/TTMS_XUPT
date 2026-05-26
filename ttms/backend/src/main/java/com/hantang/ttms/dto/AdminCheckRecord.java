package com.hantang.ttms.dto;

import java.time.LocalDateTime;

public record AdminCheckRecord(
    Long id,
    Long ticketId,
    Integer seatRow,
    Integer seatCol,
    String playName,
    String studioName,
    LocalDateTime showTime,
    LocalDateTime verifyTime,
    String operatorName,
    String result
) {}

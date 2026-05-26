package com.hantang.ttms.dto;

import java.time.LocalDateTime;

public record AdminVerifyResult(
    Long ticketId,
    Integer seatRow,
    Integer seatCol,
    String playName,
    String studioName,
    LocalDateTime showTime,
    String status,
    String message
) {}

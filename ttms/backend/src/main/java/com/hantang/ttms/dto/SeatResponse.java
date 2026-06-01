package com.hantang.ttms.dto;

import com.hantang.ttms.domain.Status;

public record SeatResponse(
    Long id,
    Long studioId,
    Integer rowNo,
    Integer colNo,
    Status status
) {}

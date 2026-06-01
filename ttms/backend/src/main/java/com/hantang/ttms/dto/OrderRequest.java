package com.hantang.ttms.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record OrderRequest(
    Long customerId,
    Long employeeId,
    @NotEmpty List<Long> ticketIds
) {}

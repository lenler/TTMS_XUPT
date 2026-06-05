package com.hantang.ttms.dto;

import java.math.BigDecimal;

import com.hantang.ttms.domain.Status;

public record UserResponse(
    Long id,
    String username,
    String employeeNo,
    String name,
    String position,
    String phone,
    String email,
    Integer gender,
    BigDecimal balance,
    Status status
) {}

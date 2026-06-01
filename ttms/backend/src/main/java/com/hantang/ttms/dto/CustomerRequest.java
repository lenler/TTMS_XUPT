package com.hantang.ttms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
    @NotBlank String username,
    String password,
    String name,
    String phone,
    @Email String email,
    BigDecimal balance
) {}

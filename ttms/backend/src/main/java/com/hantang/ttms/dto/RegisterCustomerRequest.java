package com.hantang.ttms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterCustomerRequest(
    @NotBlank String username,
    @NotBlank String password,
    String name,
    String phone,
    @Email String email
) {}

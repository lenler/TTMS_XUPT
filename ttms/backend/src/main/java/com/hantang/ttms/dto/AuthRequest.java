package com.hantang.ttms.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
    @NotBlank String username,
    @NotBlank String password,
    String userType
) {}

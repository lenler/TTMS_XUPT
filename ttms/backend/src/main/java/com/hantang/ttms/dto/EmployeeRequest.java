package com.hantang.ttms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmployeeRequest(
    @NotBlank String employeeNo,
    @NotBlank String name,
    @NotBlank String position,
    String phone,
    @Email String email,
    String password
) {}

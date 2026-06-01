package com.hantang.ttms.dto;

public record AdminEmployeeRequest(
    String employeeNo,
    String name,
    Integer gender,
    String phone,
    String email,
    Integer positionId,
    String password
) {}

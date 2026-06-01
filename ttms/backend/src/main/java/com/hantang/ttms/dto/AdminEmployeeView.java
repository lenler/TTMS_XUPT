package com.hantang.ttms.dto;

public record AdminEmployeeView(
    Long id,
    String employeeNo,
    String name,
    int gender,
    String phone,
    String email,
    int positionId,
    String positionName,
    int status
) {}

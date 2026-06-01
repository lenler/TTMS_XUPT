package com.hantang.ttms.dto;

public record AdminStudioView(
    Long id,
    String name,
    Integer rowCount,
    Integer colCount,
    String introduction,
    int status
) {}

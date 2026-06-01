package com.hantang.ttms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudioRequest(
    @NotBlank String name,
    @NotNull @Min(1) Integer rowCount,
    @NotNull @Min(1) Integer colCount,
    String introduction
) {}

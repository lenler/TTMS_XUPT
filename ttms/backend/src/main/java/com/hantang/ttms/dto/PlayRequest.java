package com.hantang.ttms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlayRequest(
    @NotBlank String type,
    @NotBlank String language,
    @NotBlank String name,
    String introduction,
    String posterUrl,
    String trailerUrl,
    @NotNull @Min(1) Integer durationMinutes,
    @NotNull BigDecimal basePrice
) {}

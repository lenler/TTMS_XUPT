package com.hantang.ttms.dto;

import com.hantang.ttms.domain.Status;

import jakarta.validation.constraints.NotNull;

public record SeatRequest(@NotNull Status status) {}

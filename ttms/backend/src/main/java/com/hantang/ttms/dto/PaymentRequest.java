package com.hantang.ttms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(@NotNull BigDecimal paidAmount) {}

package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record AdminCustomerView(
    Long id,
    String name,
    int gender,
    String phone,
    String email,
    String username,
    BigDecimal balance,
    BigDecimal rechargeTotal,
    long rechargeCount,
    int status
) {}

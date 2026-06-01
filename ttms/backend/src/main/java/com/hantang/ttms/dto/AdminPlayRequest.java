package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record AdminPlayRequest(
    Integer typeId,
    Integer langId,
    String name,
    String introduction,
    String poster,
    String video,
    Integer duration,
    BigDecimal basePrice
) {}

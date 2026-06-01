package com.hantang.ttms.dto;

import java.math.BigDecimal;

public record AdminPlayView(
    Long id,
    int typeId,
    String typeName,
    int langId,
    String langName,
    String name,
    String introduction,
    String poster,
    String video,
    Integer duration,
    BigDecimal basePrice,
    int status
) {}

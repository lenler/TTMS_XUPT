package com.hantang.ttms.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdminRefundResult(
    Long saleId,
    List<Long> refundedTickets,
    BigDecimal refundAmount,
    String orderStatus,
    String ticketStatus
) {}

package com.hantang.ttms.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;

public interface SaleService {
    SaleResponse placeOrder(OrderRequest request);
    SaleResponse makePayment(Long saleId, BigDecimal paidAmount);
    SaleResponse refund(Long saleId);
    SaleResponse get(Long saleId);
    List<SaleResponse> list(LocalDate date, Long employeeId, Long customerId);
}

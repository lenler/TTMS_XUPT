package com.hantang.ttms.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.PaymentRequest;
import com.hantang.ttms.dto.SaleResponse;
import com.hantang.ttms.service.SaleService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ApiResponse<List<SaleResponse>> list(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Long employeeId,
        @RequestParam(required = false) Long customerId
    ) {
        return ApiResponse.ok(saleService.list(date, employeeId, customerId));
    }

    @GetMapping("/{id}")
    public ApiResponse<SaleResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(saleService.get(id));
    }

    @PostMapping("/orders")
    public ApiResponse<SaleResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.ok(saleService.placeOrder(request));
    }

    @PostMapping("/{id}/payments")
    public ApiResponse<SaleResponse> makePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.ok(saleService.makePayment(id, request.paidAmount()));
    }

    @PostMapping("/{id}/refund")
    public ApiResponse<SaleResponse> refund(@PathVariable Long id) {
        return ApiResponse.ok(saleService.refund(id));
    }
}

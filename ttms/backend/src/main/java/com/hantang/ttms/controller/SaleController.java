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

/**
 * 销售控制器，处理售票全流程：查询订单、下单锁座、支付、退票。
 *
 * <p>基础路径：{@code /sales}</p>
 * <ul>
 *   <li>GET /sales —— 查询订单列表（可按日期、售票员、观众筛选）</li>
 *   <li>GET /sales/{id} —— 查询单个订单详情</li>
 *   <li>POST /sales/orders —— 下单锁座</li>
 *   <li>POST /sales/{id}/payments —— 支付订单</li>
 *   <li>POST /sales/{id}/refund —— 退票退款</li>
 * </ul>
 */
@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleService saleService;

    /**
     * 构造销售控制器，注入销售服务。
     *
     * @param saleService 销售服务
     */
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * 查询订单列表，支持按日期、售票员、观众多条件筛选。
     *
     * <p>GET /sales</p>
     *
     * @param date       订单日期，可选
     * @param employeeId 售票员 ID，可选
     * @param customerId 观众 ID，可选
     * @return 订单列表，每个订单包含票务状态、金额等详情
     */
    @GetMapping
    public ApiResponse<List<SaleResponse>> list(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Long employeeId,
        @RequestParam(required = false) Long customerId
    ) {
        return ApiResponse.ok(saleService.list(date, employeeId, customerId));
    }

    /**
     * 查询单个订单详情。
     *
     * <p>GET /sales/{id}</p>
     *
     * @param id 订单 ID（路径变量）
     * @return 订单详情，包含票务信息和支付状态
     */
    @GetMapping("/{id}")
    public ApiResponse<SaleResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(saleService.get(id));
    }

    /**
     * 下单锁座：根据所选场次和座位生成订单，并将座位状态锁定。
     *
     * <p>POST /sales/orders</p>
     *
     * @param request 下单请求体，包含场次 ID、座位 ID 列表、观众 ID 等
     * @return 生成的订单信息，状态为已锁定待支付
     */
    @PostMapping("/orders")
    public ApiResponse<SaleResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        return ApiResponse.ok(saleService.placeOrder(request));
    }

    /**
     * 支付订单：对已锁定的订单完成支付，更新座位状态为已售出。
     *
     * <p>POST /sales/{id}/payments</p>
     *
     * @param id      订单 ID（路径变量）
     * @param request 支付请求体，包含支付金额
     * @return 支付完成后的订单信息，状态为已支付
     */
    @PostMapping("/{id}/payments")
    public ApiResponse<SaleResponse> makePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.ok(saleService.makePayment(id, request.paidAmount()));
    }

    /**
     * 退票退款：对已支付的订单执行退票，恢复座位为可用状态。
     *
     * <p>POST /sales/{id}/refund</p>
     *
     * @param id 订单 ID（路径变量）
     * @return 退票后的订单信息，状态为已退款
     */
    @PostMapping("/{id}/refund")
    public ApiResponse<SaleResponse> refund(@PathVariable Long id) {
        return ApiResponse.ok(saleService.refund(id));
    }
}

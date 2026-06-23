package com.hantang.ttms.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.hantang.ttms.dto.OrderRequest;
import com.hantang.ttms.dto.SaleResponse;

/**
 * 售票/退票业务服务接口
 *
 * 处理完整的售票生命周期：
 * - 下单（placeOrder）：锁座 → 创建订单 → 返回订单信息
 * - 收款（makePayment）：收款 → 改票状态为 SOLD → 改订单状态为 PAID
 * - 退票（refund）：退款 → 改票状态为 REFUNDED → 改订单状态为 REFUNDED
 *
 * 支持柜台售票（柜台收款）和线上售票（钱包扣款）两种模式
 */
public interface SaleService {
    /**
     * 下单——选座后锁定座位并创建销售订单
     *
     * 流程：
     * 1. 校验请求参数（票 ID 列表非空、客户/员工至少存在其一）
     * 2. 逐张票锁定：使用乐观锁将票状态从 AVAILABLE → LOCKED
     * 3. 创建订单（Sale）和订单明细（SaleItem）
     *
     * @param request 下单请求（票 ID 列表、客户 ID、员工 ID、销售渠道）
     * @return 创建成功的订单响应（含订单 ID、金额、明细）
     */
    SaleResponse placeOrder(OrderRequest request);

    /**
     * 收款确认——支付完成后确认收款并出票
     *
     * 流程：
     * 1. 校验订单状态为 PENDING_PAYMENT
     * 2. 线上支付：从客户钱包余额扣款
     * 3. 柜台支付：记录收款和找零
     * 4. 更新票状态 LOCKED → SOLD
     * 5. 更新订单状态 PENDING_PAYMENT → PAID
     *
     * @param saleId 订单 ID
     * @param paidAmount 实收金额（柜台售票场景传入，线上支付可传订单总金额）
     * @return 更新后的订单响应
     */
    SaleResponse makePayment(Long saleId, BigDecimal paidAmount);

    /**
     * 退票——对已支付的订单执行退款
     *
     * 流程：
     * 1. 校验订单状态为 PAID
     * 2. 线上支付退票：退款到客户钱包余额
     * 3. 柜台售票退票：生成退款订单记录
     * 4. 更新票状态 SOLD → REFUNDED
     * 5. 更新订单状态 PAID → REFUNDED
     *
     * @param saleId 订单 ID
     * @return 退款后的订单响应
     */
    SaleResponse refund(Long saleId);

    /**
     * 查询单个订单详情
     *
     * @param saleId 订单 ID
     * @return 订单完整信息（含明细列表）
     */
    SaleResponse get(Long saleId);

    /**
     * 查询订单列表，支持按日期、员工、客户筛选
     *
     * @param date 交易日期，可选
     * @param employeeId 操作员工 ID，可选
     * @param customerId 购买客户 ID，可选
     * @return 符合条件的订单列表
     */
    List<SaleResponse> list(LocalDate date, Long employeeId, Long customerId);
}

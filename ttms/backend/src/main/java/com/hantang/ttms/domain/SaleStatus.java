package com.hantang.ttms.domain;

/**
 * 销售订单状态枚举
 *
 * 定义销售订单（Sale）的生命周期状态：
 *
 * PENDING_PAYMENT → PAID → REFUNDED
 *                  ↓
 *              CANCELLED
 *
 * 状态说明：
 * - PENDING_PAYMENT：待支付（订单创建后的初始状态）
 * - PAID：已支付（付款完成）
 * - REFUNDED：已退款（退票流程完成后）
 * - CANCELLED：已取消（超时未支付或手动取消）
 */
public enum SaleStatus {
    /** 待支付——订单创建后等待付款 */
    PENDING_PAYMENT,
    /** 已支付——付款完成，出票成功 */
    PAID,
    /** 已退款——退票流程完成，款项已退回客户 */
    REFUNDED,
    /** 已取消——超时未支付或手动取消 */
    CANCELLED
}

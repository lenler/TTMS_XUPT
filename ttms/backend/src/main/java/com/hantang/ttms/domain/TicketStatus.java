package com.hantang.ttms.domain;

/**
 * 票状态枚举
 *
 * 定义一张演出票在整个生命周期中的状态流转：
 *
 * AVAILABLE → LOCKED → SOLD → CHECKED
 *                  ↓
 *              AVAILABLE（锁座超时释放）
 *
 * SOLD → REFUNDED（退票）
 *
 * 状态说明：
 * - AVAILABLE：可售，初始状态
 * - LOCKED：已被锁定（用户选座但未支付，超时后自动释放）
 * - SOLD：已售出（支付完成）
 * - CHECKED：已验票（观众入场核销）
 * - REFUNDED：已退票
 * - VOIDED：已作废（排期取消等异常场景）
 */
public enum TicketStatus {
    /** 可售——票初始状态，可被锁定或购买 */
    AVAILABLE,
    /** 已锁定——用户选座后暂时锁定，防止重复购买，超时后释放回 AVAILABLE */
    LOCKED,
    /** 已售出——支付完成，票已归属某客户 */
    SOLD,
    /** 已验票——观众已入场核销 */
    CHECKED,
    /** 已退票——客户退票后退款完成 */
    REFUNDED,
    /** 已作废——排期取消等异常场景 */
    VOIDED
}

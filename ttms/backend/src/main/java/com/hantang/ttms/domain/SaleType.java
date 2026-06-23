package com.hantang.ttms.domain;

/**
 * 销售渠道类型枚举
 *
 * 区分订单来自哪个销售渠道：
 * - COUNTER：柜台售票（由售票员在管理端操作）
 * - ONLINE：线上售票（由观众在观众端在线购买）
 * - REFUND：退票记录（由退票操作生成的负向流水）
 *
 * 该字段用于财务统计中按渠道分类汇总
 */
public enum SaleType {
    /** 柜台售票——管理端售票员操作 */
    COUNTER,
    /** 线上售票——观众端在线购票 */
    ONLINE,
    /** 退票——退票产生的退款记录 */
    REFUND
}

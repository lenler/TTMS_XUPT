package com.hantang.ttms.domain;

/**
 * 通用启用/禁用状态枚举
 *
 * 用于剧目（Play）、演出厅（Studio）、排期（Schedule）、
 * 座位（Seat）、客户（Customer）、员工（Employee）等实体，
 * 统一使用该枚举表示"是否可用"
 *
 * ACTIVE：正常可用，DISABLED：已禁用（逻辑删除）
 */
public enum Status {
    /** 正常可用状态 */
    ACTIVE,
    /** 已禁用状态（逻辑删除，不物理删除数据） */
    DISABLED
}

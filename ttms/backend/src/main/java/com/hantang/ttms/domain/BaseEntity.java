package com.hantang.ttms.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * 实体基类（MappedSuperclass，不映射为独立表）
 *
 * 为所有数据库实体提供通用审计字段：
 * - createdAt：记录创建时间，由 JPA PrePersist 回调自动填充
 * - updatedAt：记录最后修改时间，由 JPA PrePersist / PreUpdate 回调自动填充
 *
 * 子类只需 extends BaseEntity 即可自动继承这两个时间戳字段，
 * 无需在每个实体中重复定义和维护
 */
@MappedSuperclass
public abstract class BaseEntity {
    /** 记录创建时间，插入时自动填充且不可更新 */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 记录最后修改时间，插入和更新时自动填充 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA 插入前回调
     * 自动设置创建时间和修改时间为当前时间
     */
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * JPA 更新前回调
     * 自动更新修改时间为当前时间
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** 获取记录创建时间 */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** 获取记录最后修改时间 */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

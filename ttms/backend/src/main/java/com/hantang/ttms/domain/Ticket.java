package com.hantang.ttms.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * 票实体类——系统最核心的业务实体
 * 对应数据库表 tickets
 *
 * 每张票关联一个座位（Seat）和一个演出计划（Schedule），
 * 同一个排期下的座位只能有一张票（schedule_id + seat_id 唯一约束）
 *
 * 票生命周期：
 * 排期创建时批量生成 → AVAILABLE（可售）
 * → 用户选座锁座 → LOCKED（锁定，超时释放）
 * → 支付完成 → SOLD（已售）
 * → 入场验票 → CHECKED（已验）
 * → 退票退款 → REFUNDED（已退）
 *
 * 使用 @Version 乐观锁防止并发售票时的超卖问题
 */
@Entity
@Table(name = "tickets", uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "seat_id"}))
public class Ticket extends BaseEntity {
    /** 票唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 关联的座位（多对一），通过 seat_id 外键关联 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    /** 关联的演出计划（多对一），通过 schedule_id 外键关联 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    /** 票价（元），从排期的 ticketPrice 复制而来，保证历史数据不受后续调价影响 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** 票状态：AVAILABLE / LOCKED / SOLD / CHECKED / REFUNDED / VOIDED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status = TicketStatus.AVAILABLE;

    /** 锁座时间，用于判断锁座是否超时（超时后自动释放回 AVAILABLE） */
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    /**
     * 乐观锁版本号
     * 每次更新时自动+1，防止多个售票员同时操作同一张票导致超卖
     */
    @Version
    private Long version;

    /** 获取票 ID */
    public Long getId() { return id; }
    /** 设置票 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取关联的座位 */
    public Seat getSeat() { return seat; }
    /** 设置关联的座位 */
    public void setSeat(Seat seat) { this.seat = seat; }
    /** 获取关联的演出计划 */
    public Schedule getSchedule() { return schedule; }
    /** 设置关联的演出计划 */
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    /** 获取票价 */
    public BigDecimal getPrice() { return price; }
    /** 设置票价 */
    public void setPrice(BigDecimal price) { this.price = price; }
    /** 获取票状态 */
    public TicketStatus getStatus() { return status; }
    /** 设置票状态 */
    public void setStatus(TicketStatus status) { this.status = status; }
    /** 获取锁座时间 */
    public LocalDateTime getLockTime() { return lockTime; }
    /** 设置锁座时间 */
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
    /** 获取乐观锁版本号 */
    public Long getVersion() { return version; }
    /** 设置乐观锁版本号 */
    public void setVersion(Long version) { this.version = version; }
}

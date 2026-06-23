package com.hantang.ttms.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

/**
 * 订单明细实体类——记录订单中每张票的销售信息
 * 对应数据库表 sale_items
 *
 * 每个 SaleItem 关联一张票（Ticket）和一个订单（Sale），
 * 记录该票在本次交易中的实际售价。
 *
 * sale 字段添加 @JsonIgnore 防止序列化时的循环引用
 */
@Entity
@Table(name = "sale_items")
public class SaleItem extends BaseEntity {
    /** 明细唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属订单（多对一），序列化时忽略防止循环引用 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id")
    @JsonIgnore
    private Sale sale;

    /** 关联的票（多对一），通过 ticket_id 外键关联 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    /** 该票在本次交易中的实际售价（元） */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** 获取明细 ID */
    public Long getId() { return id; }
    /** 设置明细 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取所属订单 */
    public Sale getSale() { return sale; }
    /** 设置所属订单 */
    public void setSale(Sale sale) { this.sale = sale; }
    /** 获取关联的票 */
    public Ticket getTicket() { return ticket; }
    /** 设置关联的票 */
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
    /** 获取实际售价 */
    public BigDecimal getPrice() { return price; }
    /** 设置实际售价 */
    public void setPrice(BigDecimal price) { this.price = price; }
}

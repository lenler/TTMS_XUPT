package com.hantang.ttms.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

/**
 * 销售订单实体类——记录每一笔售票/退票交易
 * 对应数据库表 sales
 *
 * 一笔订单包含：
 * - 销售渠道（COUNTER 柜台 / ONLINE 线上 / REFUND 退票）
 * - 操作员工和购买客户（均可为空——线上购票无员工，退票无客户）
 * - 实收金额和找零金额
 * - 订单明细列表（SaleItem，包含具体的票信息）
 *
 * 订单状态流转：PENDING_PAYMENT → PAID → REFUNDED / CANCELLED
 */
@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {
    /** 订单唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作员工（多对一），柜台售票时为售票员，线上售票时为空 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /** 购买客户（多对一），线上购票时为下单用户，退票时为空 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /** 交易时间，默认为当前时间 */
    @Column(nullable = false)
    private LocalDateTime saleTime = LocalDateTime.now();

    /** 实收金额（元），客户实际支付的金额 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    /** 找零金额（元），仅柜台售票场景使用 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal changeAmount = BigDecimal.ZERO;

    /** 销售渠道：COUNTER（柜台）/ ONLINE（线上）/ REFUND（退票） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SaleType saleType;

    /** 订单状态：PENDING_PAYMENT / PAID / REFUNDED / CANCELLED */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SaleStatus status = SaleStatus.PENDING_PAYMENT;

    /** 订单明细列表（一对多），包含本次交易的每张票信息，级联持久化 */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> items = new ArrayList<>();

    /** 获取订单 ID */
    public Long getId() { return id; }
    /** 设置订单 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取操作员工 */
    public Employee getEmployee() { return employee; }
    /** 设置操作员工 */
    public void setEmployee(Employee employee) { this.employee = employee; }
    /** 获取员工 ID（便捷方法，员工为空时返回 null） */
    public Long getEmployeeId() { return employee == null ? null : employee.getId(); }
    /** 获取购买客户 */
    public Customer getCustomer() { return customer; }
    /** 设置购买客户 */
    public void setCustomer(Customer customer) { this.customer = customer; }
    /** 获取客户 ID（便捷方法，客户为空时返回 null） */
    public Long getCustomerId() { return customer == null ? null : customer.getId(); }
    /** 获取交易时间 */
    public LocalDateTime getSaleTime() { return saleTime; }
    /** 设置交易时间 */
    public void setSaleTime(LocalDateTime saleTime) { this.saleTime = saleTime; }
    /** 获取实收金额 */
    public BigDecimal getPaidAmount() { return paidAmount; }
    /** 设置实收金额 */
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    /** 获取找零金额 */
    public BigDecimal getChangeAmount() { return changeAmount; }
    /** 设置找零金额 */
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    /** 获取销售渠道 */
    public SaleType getSaleType() { return saleType; }
    /** 设置销售渠道 */
    public void setSaleType(SaleType saleType) { this.saleType = saleType; }
    /** 获取订单状态 */
    public SaleStatus getStatus() { return status; }
    /** 设置订单状态 */
    public void setStatus(SaleStatus status) { this.status = status; }
    /** 获取订单明细列表 */
    public List<SaleItem> getItems() { return items; }
    /** 设置订单明细列表 */
    public void setItems(List<SaleItem> items) { this.items = items; }
}

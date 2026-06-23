package com.hantang.ttms.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户充值记录实体类
 * 对应数据库表 customer_recharges
 *
 * 记录客户每次账户充值的金额和充值后的余额，
 * 用于财务对账和客户消费历史查询。
 *
 * 注意：该类未使用 JPA 注解（为非 Entity），
 * 由 MyBatis Mapper 直接操作。
 */
public class CustomerRecharge {
    /** 充值记录唯一标识，自增主键 */
    private Long id;

    /** 充值客户 ID，关联 customers 表 */
    private Long customerId;

    /** 充值金额（元） */
    private BigDecimal amount;

    /** 充值后的账户余额（元），用于审计追踪 */
    private BigDecimal balanceAfter;

    /** 充值时间，默认为当前时间 */
    private LocalDateTime rechargeTime = LocalDateTime.now();

    /** 获取记录 ID */
    public Long getId() { return id; }
    /** 设置记录 ID */
    public void setId(Long id) { this.id = id; }
    /** 获取客户 ID */
    public Long getCustomerId() { return customerId; }
    /** 设置客户 ID */
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    /** 获取充值金额 */
    public BigDecimal getAmount() { return amount; }
    /** 设置充值金额 */
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    /** 获取充值后余额 */
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    /** 设置充值后余额 */
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    /** 获取充值时间 */
    public LocalDateTime getRechargeTime() { return rechargeTime; }
    /** 设置充值时间 */
    public void setRechargeTime(LocalDateTime rechargeTime) { this.rechargeTime = rechargeTime; }
}

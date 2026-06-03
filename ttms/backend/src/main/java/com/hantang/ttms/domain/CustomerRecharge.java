package com.hantang.ttms.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerRecharge {
    private Long id;
    private Long customerId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime rechargeTime = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    public LocalDateTime getRechargeTime() { return rechargeTime; }
    public void setRechargeTime(LocalDateTime rechargeTime) { this.rechargeTime = rechargeTime; }
}

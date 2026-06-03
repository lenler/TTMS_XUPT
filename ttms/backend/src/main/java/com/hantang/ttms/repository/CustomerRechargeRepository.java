package com.hantang.ttms.repository;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.hantang.ttms.domain.CustomerRecharge;

public interface CustomerRechargeRepository {
    @Insert("""
        INSERT INTO customer_recharges (customer_id, amount, balance_after, recharge_time, created_at, updated_at)
        VALUES (#{customerId}, #{amount}, #{balanceAfter}, #{rechargeTime}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerRecharge recharge);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM customer_recharges WHERE customer_id = #{customerId}")
    BigDecimal sumAmountByCustomerId(Long customerId);

    @Select("SELECT COUNT(*) FROM customer_recharges WHERE customer_id = #{customerId}")
    long countByCustomerId(Long customerId);
}

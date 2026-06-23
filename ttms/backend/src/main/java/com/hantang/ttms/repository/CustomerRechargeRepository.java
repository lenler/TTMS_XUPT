package com.hantang.ttms.repository;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.hantang.ttms.domain.CustomerRecharge;

/**
 * 观众充值记录数据访问层（MyBatis Mapper）。
 *
 * <p>负责 customer_recharges 表的插入和查询操作，提供按观众 ID 汇总
 * 充值总额及统计充值次数功能。</p>
 *
 * @author XUPT
 */
public interface CustomerRechargeRepository {

    /**
     * 新增一条观众充值记录。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param recharge 充值记录实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO customer_recharges (customer_id, amount, balance_after, recharge_time, created_at, updated_at)
        VALUES (#{customerId}, #{amount}, #{balanceAfter}, #{rechargeTime}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerRecharge recharge);

    /**
     * 汇总指定观众的累计充值金额。
     *
     * <p>使用 COALESCE 确保无记录时返回 0 而非 null。</p>
     *
     * @param customerId 观众 ID
     * @return 累计充值金额（无记录时返回 0）
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM customer_recharges WHERE customer_id = #{customerId}")
    BigDecimal sumAmountByCustomerId(Long customerId);

    /**
     * 统计指定观众的充值次数。
     *
     * @param customerId 观众 ID
     * @return 充值次数
     */
    @Select("SELECT COUNT(*) FROM customer_recharges WHERE customer_id = #{customerId}")
    long countByCustomerId(Long customerId);
}

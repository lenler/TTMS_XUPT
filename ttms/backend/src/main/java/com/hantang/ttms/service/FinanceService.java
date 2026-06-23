package com.hantang.ttms.service;

import java.time.LocalDate;

import com.hantang.ttms.dto.FinanceSummaryResponse;

/**
 * 财务统计业务服务接口
 *
 * 提供三个维度的财务汇总：
 * - 日报：按日期 + 可选员工筛选
 * - 剧院汇总：按日期区间统计
 * - 排期汇总：按指定演出计划统计
 *
 * 统计数据包括：售票金额、退票金额、客单价、销售量等
 */
public interface FinanceService {
    /**
     * 日报统计——按日期汇总售票/退票数据
     *
     * 统计项：
     * - 总销售额（售票金额合计）
     * - 总退票额（退票金额合计）
     * - 净收入（销售额 - 退票额）
     * - 售票数、退票数
     * - 员工维度明细（按员工分组，用于员工销售排名）
     *
     * @param date 统计日期，必填
     * @param employeeId 员工 ID，可选（筛选特定员工的销售数据）
     * @return 财务汇总响应
     */
    FinanceSummaryResponse dailySummary(LocalDate date, Long employeeId);

    /**
     * 剧院汇总统计——按日期区间汇总
     *
     * 统计项与日报相同，但按日期区间聚合
     *
     * @param startDate 统计开始日期（含）
     * @param endDate 统计结束日期（含）
     * @return 财务汇总响应
     */
    FinanceSummaryResponse theaterSummary(LocalDate startDate, LocalDate endDate);

    /**
     * 排期汇总统计——按指定演出计划统计
     *
     * 统计该排期的售票数、退票数、总销售额等
     *
     * @param scheduleId 排期 ID
     * @return 财务汇总响应
     */
    FinanceSummaryResponse scheduleSummary(Long scheduleId);
}

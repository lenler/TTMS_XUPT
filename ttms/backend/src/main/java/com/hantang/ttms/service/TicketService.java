package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.TicketResponse;

/**
 * 票务查询与验票业务服务接口
 *
 * 负责票的查询和入场验票操作：
 * - 按排期查询票列表（用于管理端查看售票情况）
 * - 验票入场（观众入场时核销票）
 * - 单张票查询
 *
 * 票的生成和状态变更由 ScheduleService（生成票）和 SaleService（售票/退票）处理
 */
public interface TicketService {
    /**
     * 查询指定排期的全部票
     *
     * 返回票列表及其关联的座位信息和当前状态，
     * 用于管理端查看售票情况（选座图、已售/可售统计）
     *
     * @param scheduleId 排期 ID
     * @return 票响应列表（含座位行列、票状态、价格）
     */
    List<TicketResponse> listBySchedule(Long scheduleId);

    /**
     * 验票入场——核销一张已售出的票
     *
     * 校验规则：
     * - 票状态必须为 SOLD（已售），其他状态不可验票
     * - 改票状态 SOLD → CHECKED
     * - 返回验票结果（含座位信息，用于引导观众入座）
     *
     * @param ticketId 票 ID
     * @return 验票后的票信息（含座位行列号）
     */
    TicketResponse checkIn(Long ticketId);

    /**
     * 查询单张票的详细信息
     *
     * @param ticketId 票 ID
     * @return 票的完整信息
     */
    TicketResponse get(Long ticketId);
}

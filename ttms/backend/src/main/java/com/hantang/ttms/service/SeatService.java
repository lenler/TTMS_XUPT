package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.SeatRequest;
import com.hantang.ttms.dto.SeatResponse;

/**
 * 座位管理业务服务接口
 *
 * 负责演出厅座位的查询和状态管理：
 * - 按演出厅查询全部座位（按行列排序）
 * - 更新座位状态（启用/禁用单个座位）
 *
 * 座位由演出厅创建时自动生成，此接口不提供创建/删除操作
 */
public interface SeatService {
    /**
     * 查询演出厅的全部座位（按行列排序）
     *
     * @param studioId 演出厅 ID
     * @return 座位列表，按 (rowNo ASC, colNo ASC) 排序
     */
    List<SeatResponse> listByStudio(Long studioId);

    /**
     * 更新单个座位的状态
     *
     * 可用于禁用损坏的座位或重新启用已修复的座位
     *
     * @param id 座位 ID
     * @param request 座位更新请求（状态字段）
     * @return 更新后的座位信息
     */
    SeatResponse update(Long id, SeatRequest request);
}

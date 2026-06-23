package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Seat;
import com.hantang.ttms.dto.SeatRequest;
import com.hantang.ttms.dto.SeatResponse;
import com.hantang.ttms.repository.SeatRepository;
import com.hantang.ttms.service.SeatService;

/**
 * 座位管理业务服务实现
 *
 * 负责演出厅座位的查询和状态变更：
 * - 按演出厅查询座位（按行列排序，用于渲染座位图网格）
 * - 更新座位状态（启用/禁用单个座位）
 *
 * 座位由演出厅创建时在 StudioServiceImpl.rebuildSeats() 中自动生成，
 * 本服务不负责座位的创建和删除。
 */
@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;

    /** 构造函数注入座位数据访问层 */
    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * {@inheritDoc}
     *
     * 返回结果按 (rowNo ASC, colNo ASC) 排序，
     * 前端可直接按行列号渲染座位网格
     */
    @Override
    public List<SeatResponse> listByStudio(Long studioId) {
        return seatRepository.findByStudioIdOrderByRowNoAscColNoAsc(studioId).stream()
            .map(this::toResponse)
            .toList();
    }

    /**
     * {@inheritDoc}
     *
     * 仅更新状态字段（ACTIVE/DISABLED），不影响行列坐标和所属演出厅
     */
    @Override
    @Transactional
    public SeatResponse update(Long id, SeatRequest request) {
        Seat seat = seatRepository.findById(id)
            .orElseThrow(() -> new BusinessException("座位不存在"));
        seat.setStatus(request.status());
        return toResponse(seatRepository.save(seat));
    }

    /**
     * 将 Seat 实体转换为响应 DTO
     * 包含座位 ID、演出厅 ID、行列号和状态
     */
    private SeatResponse toResponse(Seat seat) {
        return new SeatResponse(
            seat.getId(),
            seat.getStudio().getId(),
            seat.getRowNo(),
            seat.getColNo(),
            seat.getStatus()
        );
    }
}

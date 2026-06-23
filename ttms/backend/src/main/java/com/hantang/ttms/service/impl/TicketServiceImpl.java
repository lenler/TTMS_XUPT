package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.TicketMapper;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.TicketService;

/**
 * 票务查询与验票业务服务实现
 *
 * 负责票的查询和入场验票操作：
 *
 * 验票规则：
 * - 仅 SOLD（已售出）状态的票可验票
 * - 验票后状态变为 CHECKED
 * - 已验票不可退票（在 SaleServiceImpl.refund 中校验）
 *
 * 票的生成（ScheduleServiceImpl.generateTickets）
 * 和售票状态变更（SaleServiceImpl）由其他服务处理
 */
@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    /** 构造函数注入票数据访问层 */
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * {@inheritDoc}
     *
     * 返回结果包含票的座位行列信息（通过 TicketMapper 转换），
     * 用于前端渲染选座图（以不同颜色区分 AVAILABLE/LOCKED/SOLD/CHECKED 状态）
     */
    @Override
    public List<TicketResponse> listBySchedule(Long scheduleId) {
        return ticketRepository.findByScheduleId(scheduleId).stream()
            .map(TicketMapper::toTicketResponse)
            .toList();
    }

    /**
     * {@inheritDoc}
     *
     * 验票后票状态变为 CHECKED，不可再次验票或退票
     */
    @Override
    @Transactional
    public TicketResponse checkIn(Long ticketId) {
        Ticket ticket = ticketRepository.findDetailedById(ticketId)
            .orElseThrow(() -> new BusinessException("票据不存在"));
        // 仅已售状态的票可验票
        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new BusinessException("票据状态不允许入场");
        }
        ticket.setStatus(TicketStatus.CHECKED);
        return TicketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

    /**
     * {@inheritDoc}
     *
     * 查询单张票的详细信息，包括关联的座位行列号和排期信息
     */
    @Override
    public TicketResponse get(Long ticketId) {
        Ticket ticket = ticketRepository.findDetailedById(ticketId)
            .orElseThrow(() -> new BusinessException("票据不存在"));
        return TicketMapper.toTicketResponse(ticket);
    }
}

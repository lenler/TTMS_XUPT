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

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<TicketResponse> listBySchedule(Long scheduleId) {
        return ticketRepository.findByScheduleId(scheduleId).stream()
            .map(TicketMapper::toTicketResponse)
            .toList();
    }

    @Override
    @Transactional
    public TicketResponse checkIn(Long ticketId) {
        Ticket ticket = ticketRepository.findDetailedById(ticketId).orElseThrow(() -> new BusinessException("票据不存在"));
        if (ticket.getStatus() != TicketStatus.SOLD) {
            throw new BusinessException("票据状态不允许入场");
        }
        ticket.setStatus(TicketStatus.CHECKED);
        return TicketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

    @Override
    public TicketResponse get(Long ticketId) {
        Ticket ticket = ticketRepository.findDetailedById(ticketId).orElseThrow(() -> new BusinessException("票据不存在"));
        return TicketMapper.toTicketResponse(ticket);
    }
}

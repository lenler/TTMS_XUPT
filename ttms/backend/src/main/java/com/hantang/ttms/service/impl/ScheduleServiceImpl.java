package com.hantang.ttms.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Seat;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.ScheduleRequest;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.SeatRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.ScheduleService;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final StudioRepository studioRepository;
    private final PlayRepository playRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

    public ScheduleServiceImpl(
        ScheduleRepository scheduleRepository,
        StudioRepository studioRepository,
        PlayRepository playRepository,
        SeatRepository seatRepository,
        TicketRepository ticketRepository
    ) {
        this.scheduleRepository = scheduleRepository;
        this.studioRepository = studioRepository;
        this.playRepository = playRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<ScheduleResponse> listByPlay(Long playId) {
        List<Schedule> schedules = playId == null ? scheduleRepository.findAll() : scheduleRepository.findByPlayId(playId);
        return schedules.stream().map(this::toResponse).toList();
    }

    @Override
    public List<ScheduleResponse> listPublic(Long playId) {
        List<Schedule> schedules = playId == null ? scheduleRepository.findByStatus(Status.ACTIVE) : scheduleRepository.findByPlayId(playId);
        return schedules.stream()
            .filter(schedule -> schedule.getStatus() == Status.ACTIVE)
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public ScheduleResponse create(ScheduleRequest request) {
        Studio studio = studioRepository.findById(request.studioId()).orElseThrow(() -> new BusinessException("演出厅不存在"));
        Play play = playRepository.findById(request.playId()).orElseThrow(() -> new BusinessException("剧目不存在"));
        if (studio.getStatus() != Status.ACTIVE || play.getStatus() != Status.ACTIVE) {
            throw new BusinessException("演出厅或剧目已停用");
        }
        if (hasConflict(studio.getId(), request.showTime(), play.getDurationMinutes())) {
            throw new BusinessException("演出计划与已有场次冲突");
        }

        Schedule schedule = new Schedule();
        schedule.setStudio(studio);
        schedule.setPlay(play);
        schedule.setShowTime(request.showTime());
        schedule.setTicketPrice(request.ticketPrice());
        Schedule saved = scheduleRepository.save(schedule);
        generateTickets(saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void generateTickets(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new BusinessException("演出计划不存在"));
        if (!ticketRepository.findByScheduleId(scheduleId).isEmpty()) {
            return;
        }
        List<Seat> seats = seatRepository.findByStudioIdOrderByRowNoAscColNoAsc(schedule.getStudio().getId());
        for (Seat seat : seats) {
            Ticket ticket = new Ticket();
            ticket.setSchedule(schedule);
            ticket.setSeat(seat);
            ticket.setPrice(schedule.getTicketPrice());
            ticketRepository.save(ticket);
        }
    }

    private boolean hasConflict(Long studioId, LocalDateTime showTime, Integer durationMinutes) {
        LocalDateTime newStart = showTime;
        LocalDateTime newEnd = showTime.plusMinutes(durationMinutes);
        return scheduleRepository.findByStudioIdAndStatus(studioId, Status.ACTIVE).stream()
            .anyMatch(existing -> {
                LocalDateTime existingStart = existing.getShowTime();
                LocalDateTime existingEnd = existing.getShowTime().plusMinutes(existing.getPlay().getDurationMinutes());
                return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            });
    }

    private ScheduleResponse toResponse(Schedule schedule) {
        long total = ticketRepository.countByScheduleId(schedule.getId());
        long available = ticketRepository.countByScheduleIdAndStatus(schedule.getId(), TicketStatus.AVAILABLE);
        return new ScheduleResponse(
            schedule.getId(),
            schedule.getStudio().getId(),
            schedule.getStudio().getName(),
            schedule.getPlay().getId(),
            schedule.getPlay().getName(),
            schedule.getShowTime(),
            schedule.getTicketPrice(),
            schedule.getStatus(),
            total,
            available
        );
    }
}

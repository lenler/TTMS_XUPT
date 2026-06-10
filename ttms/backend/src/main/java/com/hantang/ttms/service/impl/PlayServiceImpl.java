package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;
import com.hantang.ttms.dto.PlayRequest;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.repository.ScheduleRepository;
import com.hantang.ttms.repository.TicketRepository;
import com.hantang.ttms.service.PlayService;

@Service
public class PlayServiceImpl implements PlayService {
    private final PlayRepository playRepository;
    private final ScheduleRepository scheduleRepository;
    private final TicketRepository ticketRepository;

    public PlayServiceImpl(
        PlayRepository playRepository,
        ScheduleRepository scheduleRepository,
        TicketRepository ticketRepository
    ) {
        this.playRepository = playRepository;
        this.scheduleRepository = scheduleRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Play> search(String name) {
        if (name == null || name.isBlank()) {
            return playRepository.findAll();
        }
        return playRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Play create(PlayRequest request) {
        Play play = new Play();
        apply(play, request);
        return playRepository.save(play);
    }

    @Override
    @Transactional
    public Play update(Long id, PlayRequest request) {
        Play play = playRepository.findById(id).orElseThrow(() -> new BusinessException("剧目不存在"));
        apply(play, request);
        return playRepository.save(play);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        Play play = playRepository.findById(id).orElseThrow(() -> new BusinessException("剧目不存在"));
        play.setStatus(Status.DISABLED);
        playRepository.save(play);

        // 级联禁用所有关联排期，并释放已锁定的票
        List<Schedule> schedules = scheduleRepository.findByPlayId(id);
        for (Schedule schedule : schedules) {
            schedule.setStatus(Status.DISABLED);
            scheduleRepository.save(schedule);

            // 释放该排期下所有已锁定的票
            List<Ticket> tickets = ticketRepository.findByScheduleId(schedule.getId());
            for (Ticket ticket : tickets) {
                if (ticket.getStatus() == TicketStatus.LOCKED) {
                    ticket.setStatus(TicketStatus.AVAILABLE);
                    ticket.setLockTime(null);
                    ticketRepository.save(ticket);
                }
            }
        }
    }

    private void apply(Play play, PlayRequest request) {
        play.setType(request.type());
        play.setLanguage(request.language());
        play.setName(request.name());
        play.setIntroduction(request.introduction());
        play.setPosterUrl(request.posterUrl());
        play.setTrailerUrl(request.trailerUrl());
        play.setDurationMinutes(request.durationMinutes());
        play.setBasePrice(request.basePrice());
    }
}

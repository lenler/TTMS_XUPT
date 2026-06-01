package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Seat;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.dto.StudioRequest;
import com.hantang.ttms.repository.SeatRepository;
import com.hantang.ttms.repository.StudioRepository;
import com.hantang.ttms.service.StudioService;

@Service
public class StudioServiceImpl implements StudioService {
    private final StudioRepository studioRepository;
    private final SeatRepository seatRepository;

    public StudioServiceImpl(StudioRepository studioRepository, SeatRepository seatRepository) {
        this.studioRepository = studioRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public List<Studio> search(String name) {
        if (name == null || name.isBlank()) {
            return studioRepository.findAll();
        }
        return studioRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Studio create(StudioRequest request) {
        Studio studio = new Studio();
        apply(studio, request);
        Studio saved = studioRepository.save(studio);
        rebuildSeats(saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Studio update(Long id, StudioRequest request) {
        Studio studio = studioRepository.findById(id).orElseThrow(() -> new BusinessException("演出厅不存在"));
        apply(studio, request);
        return studioRepository.save(studio);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        Studio studio = studioRepository.findById(id).orElseThrow(() -> new BusinessException("演出厅不存在"));
        studio.setStatus(Status.DISABLED);
        studioRepository.save(studio);
    }

    @Override
    @Transactional
    public void rebuildSeats(Long studioId) {
        Studio studio = studioRepository.findById(studioId).orElseThrow(() -> new BusinessException("演出厅不存在"));
        if (!seatRepository.findByStudioIdOrderByRowNoAscColNoAsc(studioId).isEmpty()) {
            return;
        }
        for (int row = 1; row <= studio.getRowCount(); row++) {
            for (int col = 1; col <= studio.getColCount(); col++) {
                Seat seat = new Seat();
                seat.setStudio(studio);
                seat.setRowNo(row);
                seat.setColNo(col);
                seatRepository.save(seat);
            }
        }
    }

    private void apply(Studio studio, StudioRequest request) {
        studio.setName(request.name());
        studio.setRowCount(request.rowCount());
        studio.setColCount(request.colCount());
        studio.setIntroduction(request.introduction());
    }
}

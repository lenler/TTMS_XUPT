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

@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public List<SeatResponse> listByStudio(Long studioId) {
        return seatRepository.findByStudioIdOrderByRowNoAscColNoAsc(studioId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public SeatResponse update(Long id, SeatRequest request) {
        Seat seat = seatRepository.findById(id).orElseThrow(() -> new BusinessException("座位不存在"));
        seat.setStatus(request.status());
        return toResponse(seatRepository.save(seat));
    }

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

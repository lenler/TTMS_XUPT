package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.SeatRequest;
import com.hantang.ttms.dto.SeatResponse;

public interface SeatService {
    List<SeatResponse> listByStudio(Long studioId);
    SeatResponse update(Long id, SeatRequest request);
}

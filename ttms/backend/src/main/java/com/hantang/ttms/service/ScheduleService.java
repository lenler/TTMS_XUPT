package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.ScheduleRequest;

public interface ScheduleService {
    List<ScheduleResponse> listByPlay(Long playId);
    List<ScheduleResponse> listPublic(Long playId);
    ScheduleResponse create(ScheduleRequest request);
    void generateTickets(Long scheduleId);
}

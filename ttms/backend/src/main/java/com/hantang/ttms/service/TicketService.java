package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.TicketResponse;

public interface TicketService {
    List<TicketResponse> listBySchedule(Long scheduleId);
    TicketResponse checkIn(Long ticketId);
    TicketResponse get(Long ticketId);
}

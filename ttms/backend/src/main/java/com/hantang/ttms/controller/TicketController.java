package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.TicketResponse;
import com.hantang.ttms.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ApiResponse<List<TicketResponse>> list(@RequestParam Long scheduleId) {
        return ApiResponse.ok(ticketService.listBySchedule(scheduleId));
    }

    @GetMapping("/{id}")
    public ApiResponse<TicketResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(ticketService.get(id));
    }

    @PostMapping("/{id}/check-in")
    public ApiResponse<TicketResponse> checkIn(@PathVariable Long id) {
        return ApiResponse.ok(ticketService.checkIn(id));
    }
}

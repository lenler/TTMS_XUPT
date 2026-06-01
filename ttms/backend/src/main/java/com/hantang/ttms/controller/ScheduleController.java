package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.dto.ScheduleRequest;
import com.hantang.ttms.service.ScheduleService;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ApiResponse<List<ScheduleResponse>> list(@RequestParam(required = false) Long playId) {
        return ApiResponse.ok(scheduleService.listByPlay(playId));
    }

    @PostMapping
    public ApiResponse<ScheduleResponse> create(@Valid @RequestBody ScheduleRequest request) {
        return ApiResponse.ok(scheduleService.create(request));
    }

    @PostMapping("/{id}/tickets")
    public ApiResponse<Void> generateTickets(@PathVariable Long id) {
        scheduleService.generateTickets(id);
        return ApiResponse.ok();
    }
}

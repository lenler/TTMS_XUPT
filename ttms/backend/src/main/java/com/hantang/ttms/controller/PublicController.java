package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.service.PlayService;
import com.hantang.ttms.service.ScheduleService;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final PlayService playService;
    private final ScheduleService scheduleService;

    public PublicController(PlayService playService, ScheduleService scheduleService) {
        this.playService = playService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/plays")
    public ApiResponse<List<Play>> plays(@RequestParam(required = false) String name) {
        return ApiResponse.ok(playService.search(name));
    }

    @GetMapping("/schedules")
    public ApiResponse<List<ScheduleResponse>> schedules(@RequestParam(required = false) Long playId) {
        return ApiResponse.ok(scheduleService.listPublic(playId));
    }
}

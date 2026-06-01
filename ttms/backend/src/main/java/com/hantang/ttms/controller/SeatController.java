package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.SeatRequest;
import com.hantang.ttms.dto.SeatResponse;
import com.hantang.ttms.service.SeatService;

@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping
    public ApiResponse<List<SeatResponse>> list(@RequestParam Long studioId) {
        return ApiResponse.ok(seatService.listByStudio(studioId));
    }

    @PutMapping("/{id}")
    public ApiResponse<SeatResponse> update(@PathVariable Long id, @Valid @RequestBody SeatRequest request) {
        return ApiResponse.ok(seatService.update(id, request));
    }
}

package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.dto.StudioRequest;
import com.hantang.ttms.service.StudioService;

@RestController
@RequestMapping("/studios")
public class StudioController {
    private final StudioService studioService;

    public StudioController(StudioService studioService) {
        this.studioService = studioService;
    }

    @GetMapping
    public ApiResponse<List<Studio>> search(@RequestParam(required = false) String name) {
        return ApiResponse.ok(studioService.search(name));
    }

    @PostMapping
    public ApiResponse<Studio> create(@Valid @RequestBody StudioRequest request) {
        return ApiResponse.ok(studioService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Studio> update(@PathVariable Long id, @Valid @RequestBody StudioRequest request) {
        return ApiResponse.ok(studioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        studioService.disable(id);
        return ApiResponse.ok();
    }
}

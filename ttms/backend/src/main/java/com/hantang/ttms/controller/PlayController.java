package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.dto.PlayRequest;
import com.hantang.ttms.service.PlayService;

@RestController
@RequestMapping("/plays")
public class PlayController {
    private final PlayService playService;

    public PlayController(PlayService playService) {
        this.playService = playService;
    }

    @GetMapping
    public ApiResponse<List<Play>> search(@RequestParam(required = false) String name) {
        return ApiResponse.ok(playService.search(name));
    }

    @PostMapping
    public ApiResponse<Play> create(@Valid @RequestBody PlayRequest request) {
        return ApiResponse.ok(playService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Play> update(@PathVariable Long id, @Valid @RequestBody PlayRequest request) {
        return ApiResponse.ok(playService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> disable(@PathVariable Long id) {
        playService.disable(id);
        return ApiResponse.ok();
    }
}

package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.SeatRequest;
import com.hantang.ttms.dto.SeatResponse;
import com.hantang.ttms.service.SeatService;

/**
 * 座位管理控制器，提供影厅座位查询和座位信息修改功能。
 *
 * <p>基础路径：{@code /seats}</p>
 * <ul>
 *   <li>GET /seats —— 查询指定影厅的全部座位</li>
 *   <li>PUT /seats/{id} —— 修改指定座位的状态或信息</li>
 * </ul>
 */
@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    /**
     * 构造座位控制器，注入座位服务。
     *
     * @param seatService 座位服务
     */
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    /**
     * 查询指定影厅的全部座位列表。
     *
     * <p>GET /seats?studioId={studioId}</p>
     *
     * @param studioId 影厅 ID（必填查询参数）
     * @return 该影厅下所有座位的列表，包含座位行号、列号、状态等信息
     */
    @GetMapping
    public ApiResponse<List<SeatResponse>> list(@RequestParam Long studioId) {
        return ApiResponse.ok(seatService.listByStudio(studioId));
    }

    /**
     * 修改指定座位的状态或信息（如损坏、维修、可用等）。
     *
     * <p>PUT /seats/{id}</p>
     *
     * @param id      座位 ID（路径变量）
     * @param request 座位修改请求体，包含要更新的座位字段
     * @return 更新后的座位信息
     */
    @PutMapping("/{id}")
    public ApiResponse<SeatResponse> update(@PathVariable Long id, @Valid @RequestBody SeatRequest request) {
        return ApiResponse.ok(seatService.update(id, request));
    }
}

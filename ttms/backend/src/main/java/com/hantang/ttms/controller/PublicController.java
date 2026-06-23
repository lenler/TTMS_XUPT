package com.hantang.ttms.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.dto.ScheduleResponse;
import com.hantang.ttms.service.PlayService;
import com.hantang.ttms.service.ScheduleService;

/**
 * 公共查询控制器，提供无需登录即可访问的剧目和演出场次查询接口，供观众端首页和浏览使用。
 *
 * <p>基础路径：{@code /public}</p>
 * <ul>
 *   <li>GET /public/plays —— 查询剧目列表（可按名称模糊搜索）</li>
 *   <li>GET /public/schedules —— 查询演出场次列表（可按剧目筛选）</li>
 * </ul>
 */
@RestController
@RequestMapping("/public")
public class PublicController {
    private final PlayService playService;
    private final ScheduleService scheduleService;

    /**
     * 构造公共查询控制器，注入剧目服务和场次服务。
     *
     * @param playService     剧目服务
     * @param scheduleService 场次服务
     */
    public PublicController(PlayService playService, ScheduleService scheduleService) {
        this.playService = playService;
        this.scheduleService = scheduleService;
    }

    /**
     * 查询剧目列表，支持按剧目名称模糊搜索。
     *
     * <p>GET /public/plays</p>
     *
     * @param name 剧目名称关键字，可选，用于模糊搜索
     * @return 剧目列表
     */
    @GetMapping("/plays")
    public ApiResponse<List<Play>> plays(@RequestParam(required = false) String name) {
        return ApiResponse.ok(playService.search(name));
    }

    /**
     * 查询演出场次列表，支持按剧目 ID 筛选。
     *
     * <p>GET /public/schedules</p>
     *
     * @param playId 剧目 ID，可选，用于筛选该剧目下的所有场次
     * @return 演出场次列表，包含场次时间、影厅、票价等信息
     */
    @GetMapping("/schedules")
    public ApiResponse<List<ScheduleResponse>> schedules(@RequestParam(required = false) Long playId) {
        return ApiResponse.ok(scheduleService.listPublic(playId));
    }
}

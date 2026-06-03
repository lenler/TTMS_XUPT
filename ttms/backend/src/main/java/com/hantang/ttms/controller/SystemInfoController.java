package com.hantang.ttms.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hantang.ttms.dto.AdminApiResponse;

/**
 * 系统信息接口 —— 返回项目元数据、运行状态等
 * 用于前端"关于"页面展示及健康监控
 *
 * @author lyd60417
 */
@RestController
@RequestMapping("/api/system")
public class SystemInfoController {

    @Value("${ttms.version:0.1.0}")
    private String appVersion;

    /**
     * 获取系统基本信息
     */
    @GetMapping("/info")
    public AdminApiResponse<Map<String, Object>> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "票枢Core · 剧目票务管理系统");
        info.put("nameEn", "Ticket Core — Troupe Ticket Management System");
        info.put("abbr", "TTMS");
        info.put("version", appVersion);
        info.put("description", "面向中小型剧院的剧目票务全流程管理平台，支持演出厅管理、剧目排期、在线选座、售票验票及财务统计等功能。");
        info.put("team", "HanTang Studio");
        info.put("developer", "lyd60417");
        info.put("javaVersion", System.getProperty("java.version", "unknown"));
        info.put("osInfo", System.getProperty("os.name", "unknown") + " " + System.getProperty("os.version", ""));
        info.put("startupTime", startupTime);
        info.put("timestamp", LocalDateTime.now().toString());
        return AdminApiResponse.ok(info);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public AdminApiResponse<Map<String, Object>> health() {
        return AdminApiResponse.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString()
        ));
    }

    private static final String startupTime = LocalDateTime.now().toString();
}

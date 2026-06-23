package com.hantang.ttms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.service.AuthService;

@RestController
@RequestMapping("/admin/api")
public class AdminAuthCompatController {
    private final AuthService authService;

    public AdminAuthCompatController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AdminApiResponse<Map<String, Object>> login(@RequestBody AuthRequest request) {
        AuthResponse auth = authService.login(new AuthRequest(request.username(), request.password(), "employee"));
        return AdminApiResponse.ok(Map.of(
            "token", "dev-token-" + auth.id(),
            "employee", Map.of(
                "id", auth.id(),
                "name", auth.name(),
                "positionName", auth.roles().isEmpty() ? "" : auth.roles().get(0),
                "roles", auth.roles()
            )
        ));
    }

    @GetMapping("/current-user")
    public AdminApiResponse<Map<String, Object>> currentUser() {
        return AdminApiResponse.ok(Map.of(
            "id", 1,
            "name", "当前用户",
            "positionName", "系统管理员",
            "roles", List.of("系统管理员")
        ));
    }

    /**
     * 获取当前用户菜单权限
     * 返回格式需与前端 authStore.fetchMenus 匹配：data.menus
     * 联调阶段返回全量菜单，后续接入 RBAC 后按角色过滤
     */
    @GetMapping("/current-user/menus")
    public AdminApiResponse<Map<String, Object>> currentUserMenus() {
        List<Map<String, Object>> menus = buildDefaultMenus();
        return AdminApiResponse.ok(Map.of("menus", menus));
    }

    /** 构建默认全量菜单树（与前端 Mock 结构一致） */
    private List<Map<String, Object>> buildDefaultMenus() {
        return List.of(
            // 工作台（一级菜单，无子级）
            Map.<String, Object>of("name", "工作台", "url", "/admin/dashboard"),
            // 剧院管理（含子菜单）
            Map.<String, Object>of("name", "剧院管理", "children", List.of(
                Map.<String, Object>of("name", "演出厅管理", "url", "/admin/studio"),
                Map.<String, Object>of("name", "剧目管理", "url", "/admin/play"),
                Map.<String, Object>of("name", "演出计划", "url", "/admin/schedule"),
                Map.<String, Object>of("name", "验票管理", "url", "/admin/check")
            )),
            // 票务管理（含子菜单）
            Map.<String, Object>of("name", "票务管理", "children", List.of(
                Map.<String, Object>of("name", "售票记录", "url", "/admin/sale"),
                Map.<String, Object>of("name", "退票处理", "url", "/admin/sale/refund")
            )),
            // 用户管理（含子菜单）
            Map.<String, Object>of("name", "用户管理", "children", List.of(
                Map.<String, Object>of("name", "员工管理", "url", "/admin/employee"),
                Map.<String, Object>of("name", "观众管理", "url", "/admin/customer")
            )),
            // 权限管理（含子菜单）
            Map.<String, Object>of("name", "权限管理", "children", List.of(
                Map.<String, Object>of("name", "角色管理", "url", "/admin/role")
            )),
            // 财务管理（含子菜单）
            Map.<String, Object>of("name", "财务管理", "children", List.of(
                Map.<String, Object>of("name", "财务统计", "url", "/admin/finance")
            )),
            // 关于（一级菜单，无子级）
            Map.<String, Object>of("name", "关于", "url", "/admin/about")
        );
    }
}

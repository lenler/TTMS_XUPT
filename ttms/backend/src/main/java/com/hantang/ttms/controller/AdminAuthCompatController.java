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

    @GetMapping("/current-user/menus")
    public AdminApiResponse<List<Map<String, Object>>> currentUserMenus() {
        return AdminApiResponse.ok(List.of());
    }
}

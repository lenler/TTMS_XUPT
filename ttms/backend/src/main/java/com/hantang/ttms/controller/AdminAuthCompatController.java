package com.hantang.ttms.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.dto.AdminApiResponse;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.service.AuthService;

/**
 * 管理端认证兼容控制器。
 * <p>
 * 为管理后台前端提供登录认证、当前用户信息查询、菜单权限查询接口。
 * 路径前缀为 {@code /admin/api}，与前端 Vite 代理规则匹配。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li><b>登录</b>：验证员工凭据，返回简易 Token 与用户基本信息</li>
 *   <li><b>当前用户</b>：返回当前登录用户信息（联调阶段返回固定值）</li>
 *   <li><b>菜单权限</b>：返回当前用户可见的菜单树（联调阶段返回全量菜单）</li>
 * </ul>
 *
 * <p>
 * 当前为联调简化实现：Token 使用 {@code dev-token-} 前缀，未接入 JWT；
 * 菜单返回全量默认菜单，后续接入 RBAC 后将按角色过滤。
 * </p>
 *
 * @author TTMS 开发团队
 * @see AuthService
 */
@RestController
@RequestMapping("/admin/api")
public class AdminAuthCompatController {
    private final AuthService authService;

    /**
     * 通过构造器注入认证服务。
     *
     * @param authService 认证业务逻辑服务
     */
    public AdminAuthCompatController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 管理端登录接口。
     * <p>
     * 验证用户名和密码，角色固定为 {@code employee}（员工登录）。
     * 登录成功后返回简易 Token 与员工基本信息（ID、姓名、职位、角色列表）。
     * 当前 Token 格式为 {@code dev-token-<userId>}，联调阶段使用，后续替换为 JWT。
     * </p>
     *
     * @param request 登录请求，包含用户名和密码
     * @return 登录结果，包含 token 与员工信息 Map
     */
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

    /**
     * 获取当前登录用户信息。
     * <p>
     * 联调阶段返回固定的系统管理员信息，无需验证 Token。
     * 后续接入完整认证体系后，将从请求头 Token 中解析当前用户身份。
     * </p>
     *
     * @return 当前用户信息 Map，包含 id、name、positionName、roles
     */
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
     * 获取当前用户菜单权限。
     * <p>
     * 返回格式需与前端 {@code authStore.fetchMenus} 匹配：{@code data.menus}。
     * 联调阶段返回全量菜单树，包含工作台、剧院管理、票务管理、用户管理、权限管理、财务管理六大模块。
     * 后续接入 RBAC 后将根据用户角色动态过滤可见菜单项。
     * </p>
     *
     * @return 菜单权限数据，包含 menus 菜单树列表
     */
    @GetMapping("/current-user/menus")
    public AdminApiResponse<Map<String, Object>> currentUserMenus() {
        List<Map<String, Object>> menus = buildDefaultMenus();
        return AdminApiResponse.ok(Map.of("menus", menus));
    }

    /**
     * 构建默认全量菜单树。
     * <p>
     * 菜单结构与前端 Mock 数据保持一致，包含以下模块：
     * </p>
     * <ul>
     *   <li>工作台（一级菜单，无子级）</li>
     *   <li>剧院管理（演出厅管理、剧目管理、演出计划、验票管理）</li>
     *   <li>票务管理（售票记录、退票处理）</li>
     *   <li>用户管理（员工管理、观众管理）</li>
     *   <li>权限管理（角色管理）</li>
     *   <li>财务管理（财务统计）</li>
     * </ul>
     *
     * @return 菜单树列表，每个菜单项包含 name 和可选的 url 或 children
     */
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

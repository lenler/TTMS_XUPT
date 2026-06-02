package com.hantang.ttms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.dto.AdminApiResponse;

/**
 * 管理端角色权限兼容控制器（联调阶段内存存储）
 */
@RestController
@RequestMapping("/admin/api")
public class AdminRoleCompatController {

    private final List<Map<String, Object>> roles = new ArrayList<>();
    private final Map<Long, List<Long>> employeeRoles = new HashMap<>();
    private int roleSeq = 3;

    /** 静态资源列表（与前端 Mock 保持一致） */
    private static final List<Map<String, Object>> RESOURCES = List.of(
        Map.of("id", 1, "type", "menu", "name", "工作台", "url", "/admin/dashboard", "parentName", ""),
        Map.of("id", 2, "type", "menu", "name", "演出厅管理", "url", "/admin/studio", "parentName", "剧院管理"),
        Map.of("id", 3, "type", "menu", "name", "剧目管理", "url", "/admin/play", "parentName", "剧院管理"),
        Map.of("id", 4, "type", "menu", "name", "演出计划", "url", "/admin/schedule", "parentName", "剧院管理"),
        Map.of("id", 5, "type", "menu", "name", "验票管理", "url", "/admin/check", "parentName", "剧院管理"),
        Map.of("id", 6, "type", "menu", "name", "售票记录", "url", "/admin/sale", "parentName", "票务管理"),
        Map.of("id", 7, "type", "menu", "name", "退票处理", "url", "/admin/sale/refund", "parentName", "票务管理"),
        Map.of("id", 8, "type", "menu", "name", "员工管理", "url", "/admin/employee", "parentName", "用户管理"),
        Map.of("id", 9, "type", "menu", "name", "观众管理", "url", "/admin/customer", "parentName", "用户管理"),
        Map.of("id", 10, "type", "menu", "name", "角色管理", "url", "/admin/role", "parentName", "权限管理"),
        Map.of("id", 11, "type", "menu", "name", "财务统计", "url", "/admin/finance", "parentName", "财务管理")
    );

    public AdminRoleCompatController() {
        // 初始化默认角色
        roles.add(roleMap(1, "系统管理员", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
        roles.add(roleMap(2, "售票员", List.of(1, 6, 7, 9)));
    }

    /** 查询角色列表 */
    @GetMapping("/roles")
    public AdminApiResponse<Map<String, Object>> listRoles() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map<String, Object> role : roles) {
            @SuppressWarnings("unchecked")
            List<Integer> rids = (List<Integer>) role.get("resourceIds");
            List<Map<String, Object>> expanded = rids.stream()
                .map(id -> RESOURCES.stream().filter(r -> r.get("id").equals(id)).findFirst().orElse(null))
                .filter(r -> r != null)
                .toList();
            Map<String, Object> copy = new HashMap<>(role);
            copy.put("resources", expanded);
            list.add(copy);
        }
        return AdminApiResponse.ok(Map.of("list", list));
    }

    /** 新增角色 */
    @PostMapping("/roles")
    public AdminApiResponse<Map<String, Object>> createRole(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> resourceIds = (List<Integer>) body.get("resourceIds");
        int id = ++roleSeq;
        roles.add(roleMap(id, (String) body.get("name"),
            resourceIds != null ? resourceIds : List.of()));
        return AdminApiResponse.ok(Map.of("id", id));
    }

    /** 修改角色 */
    @PutMapping("/roles/{id}")
    public AdminApiResponse<Void> updateRole(@PathVariable int id, @RequestBody Map<String, Object> body) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).get("id").equals(id)) {
                @SuppressWarnings("unchecked")
                List<Integer> resourceIds = (List<Integer>) body.get("resourceIds");
                if (resourceIds != null) {
                    roles.get(i).put("resourceIds", resourceIds);
                }
                if (body.containsKey("name")) {
                    roles.get(i).put("name", body.get("name"));
                }
                break;
            }
        }
        return AdminApiResponse.ok(null);
    }

    /** 删除角色 */
    @DeleteMapping("/roles/{id}")
    public AdminApiResponse<Void> deleteRole(@PathVariable int id) {
        roles.removeIf(r -> r.get("id").equals(id));
        return AdminApiResponse.ok(null);
    }

    /** 查询资源列表 */
    @GetMapping("/resources")
    public AdminApiResponse<Map<String, Object>> listResources() {
        return AdminApiResponse.ok(Map.of("list", RESOURCES));
    }

    /** 查询员工角色ID */
    @GetMapping("/employees/{empId}/roles")
    public AdminApiResponse<Map<String, Object>> getEmployeeRoles(@PathVariable Long empId) {
        List<Long> rids = employeeRoles.getOrDefault(empId, List.of());
        return AdminApiResponse.ok(Map.of("roleIds", rids));
    }

    /** 为用户分配角色 */
    @PutMapping("/employees/{empId}/roles")
    public AdminApiResponse<Void> assignEmployeeRoles(@PathVariable Long empId, @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> roleIds = (List<Integer>) body.get("roleIds");
        if (roleIds != null) {
            employeeRoles.put(empId, roleIds.stream().map(Long::valueOf).toList());
        } else {
            employeeRoles.remove(empId);
        }
        return AdminApiResponse.ok(null);
    }

    private Map<String, Object> roleMap(int id, String name, List<Integer> resourceIds) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("resourceIds", resourceIds);
        return m;
    }
}

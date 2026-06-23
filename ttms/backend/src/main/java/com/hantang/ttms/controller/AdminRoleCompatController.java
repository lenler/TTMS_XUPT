package com.hantang.ttms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.dto.AdminApiResponse;

/**
 * 管理端角色权限兼容控制器（联调阶段内存存储）。
 * <p>
 * 为管理后台前端提供角色管理和资源管理的 CRUD 接口，
 * 以及员工角色分配功能。路径前缀为 {@code /admin/api}，与前端 Vite 代理规则匹配。
 * </p>
 *
 * <h3>主要功能</h3>
 * <ul>
 *   <li><b>角色管理</b>：查询角色列表（含关联资源）、新增、修改、删除角色</li>
 *   <li><b>资源管理</b>：查询系统资源列表（菜单权限项）</li>
 *   <li><b>员工角色分配</b>：查询员工当前角色、为员工分配/修改角色</li>
 * </ul>
 *
 * <p>
 * 当前为联调阶段实现，角色、资源、员工角色分配数据均存储于内存
 * （{@code List<Map> roles}、{@code Map<Long, List<Long>> employeeRoles}）。
 * 预置两个默认角色：系统管理员（全权限）和售票员（部分权限）。
 * 后续将替换为数据库持久化方案并接入完整 RBAC 体系。
 * </p>
 *
 * @author TTMS 开发团队
 */
@RestController
@RequestMapping("/admin/api")
public class AdminRoleCompatController {

    /** 内存角色列表（联调阶段） */
    private final List<Map<String, Object>> roles = new ArrayList<>();
    /** 内存员工角色映射：员工ID → 角色ID列表（联调阶段） */
    private final Map<Long, List<Long>> employeeRoles = new HashMap<>();
    /** 角色自增序列号 */
    private int roleSeq = 3;

    /** 静态资源列表（与前端 Mock 保持一致），包含全部菜单权限项 */
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

    /**
     * 无参构造器，初始化默认角色数据。
     * <p>
     * 预置两个角色：
     * <ul>
     *   <li>系统管理员（ID=1）：拥有全部 11 项菜单权限</li>
     *   <li>售票员（ID=2）：拥有工作台、售票记录、退票处理、观众管理 4 项权限</li>
     * </ul>
     * </p>
     */
    public AdminRoleCompatController() {
        // 初始化默认角色
        roles.add(roleMap(1, "系统管理员", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)));
        roles.add(roleMap(2, "售票员", List.of(1, 6, 7, 9)));
    }

    /**
     * 查询角色列表。
     * <p>
     * 返回所有角色及其关联的完整资源信息（而非仅资源 ID），
     * 以便前端直接展示角色拥有的菜单权限。
     * </p>
     *
     * @return 包含角色列表的数据，每个角色含 id、name、resourceIds、resources 字段
     */
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

    /**
     * 新增角色。
     * <p>
     * 根据请求体中的角色名称和资源 ID 列表创建新角色。
     * 角色 ID 从自增序列号中分配（从 3 开始，因 1、2 已被预置角色使用）。
     * </p>
     *
     * @param body 请求体，包含 name（角色名称）和 resourceIds（资源 ID 列表）
     * @return 新创建的角色 ID
     */
    @PostMapping("/roles")
    public AdminApiResponse<Map<String, Object>> createRole(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> resourceIds = (List<Integer>) body.get("resourceIds");
        int id = ++roleSeq;
        roles.add(roleMap(id, (String) body.get("name"),
            resourceIds != null ? resourceIds : List.of()));
        return AdminApiResponse.ok(Map.of("id", id));
    }

    /**
     * 修改角色。
     * <p>
     * 根据角色 ID 更新角色名称和/或资源权限列表。
     * 请求体中可只传需要更新的字段，未传的字段保持不变。
     * </p>
     *
     * @param id   角色 ID（路径参数）
     * @param body 请求体，可包含 name（新角色名称）和 resourceIds（新资源 ID 列表）
     * @return 空响应表示操作成功
     */
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

    /**
     * 删除角色。
     * <p>
     * 从内存角色列表中移除指定 ID 的角色。
     * 注意：联调阶段不校验该角色是否已被分配给员工。
     * </p>
     *
     * @param id 角色 ID（路径参数）
     * @return 空响应表示操作成功
     */
    @DeleteMapping("/roles/{id}")
    public AdminApiResponse<Void> deleteRole(@PathVariable int id) {
        roles.removeIf(r -> r.get("id").equals(id));
        return AdminApiResponse.ok(null);
    }

    /**
     * 查询系统资源列表。
     * <p>
     * 返回全部菜单权限项（静态常量），供前端角色编辑页面展示可选权限。
     * 资源包含 id、type、name、url、parentName 字段。
     * </p>
     *
     * @return 包含资源列表的数据
     */
    @GetMapping("/resources")
    public AdminApiResponse<Map<String, Object>> listResources() {
        return AdminApiResponse.ok(Map.of("list", RESOURCES));
    }

    /**
     * 查询指定员工的角色 ID 列表。
     * <p>
     * 从内存映射中查找该员工当前被分配的角色。
     * </p>
     *
     * @param empId 员工 ID（路径参数）
     * @return 包含该员工角色 ID 列表的数据
     */
    @GetMapping("/employees/{empId}/roles")
    public AdminApiResponse<Map<String, Object>> getEmployeeRoles(@PathVariable Long empId) {
        List<Long> rids = employeeRoles.getOrDefault(empId, List.of());
        return AdminApiResponse.ok(Map.of("roleIds", rids));
    }

    /**
     * 为指定员工分配角色。
     * <p>
     * 覆盖式更新：传入的角色 ID 列表将完全替换该员工原有的角色分配。
     * 如果传入的 roleIds 为空或 null，则清除该员工的所有角色。
     * </p>
     *
     * @param empId 员工 ID（路径参数）
     * @param body  请求体，包含 roleIds（角色 ID 列表）
     * @return 空响应表示操作成功
     */
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

    /**
     * 构建角色 Map 对象（内部辅助方法）。
     *
     * @param id          角色 ID
     * @param name        角色名称
     * @param resourceIds 关联的资源 ID 列表
     * @return 包含 id、name、resourceIds 的角色 Map
     */
    private Map<String, Object> roleMap(int id, String name, List<Integer> resourceIds) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("resourceIds", resourceIds);
        return m;
    }
}

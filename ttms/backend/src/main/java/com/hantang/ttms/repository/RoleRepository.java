package com.hantang.ttms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Role;

/**
 * 角色 MyBatis Mapper
 */
public interface RoleRepository {

    @Select("SELECT id, name, created_at, updated_at FROM roles ORDER BY id")
    List<Role> findAll();

    @Select("SELECT id, name, created_at, updated_at FROM roles WHERE id = #{id}")
    Role findById(Long id);

    @Insert("INSERT INTO roles (name, created_at, updated_at) VALUES (#{name}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Role role);

    @Update("UPDATE roles SET name = #{name}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int update(Role role);

    @Delete("DELETE FROM roles WHERE id = #{id}")
    int deleteById(Long id);

    default Role save(Role role) {
        if (role.getId() == null) {
            insert(role);
        } else {
            update(role);
        }
        return role;
    }

    // ---- 角色-资源关联 ----

    @Select("SELECT resource_id FROM role_resources WHERE role_id = #{roleId}")
    List<Long> findResourceIdsByRoleId(Long roleId);

    @Insert("INSERT INTO role_resources (role_id, resource_id) VALUES (#{roleId}, #{resourceId})")
    int insertRoleResource(@Param("roleId") Long roleId, @Param("resourceId") Long resourceId);

    @Delete("DELETE FROM role_resources WHERE role_id = #{roleId}")
    int deleteRoleResources(Long roleId);

    // ---- 员工-角色关联 ----

    @Select("SELECT role_id FROM employee_roles WHERE employee_id = #{employeeId}")
    List<Long> findRoleIdsByEmployeeId(Long employeeId);

    @Insert("INSERT INTO employee_roles (employee_id, role_id) VALUES (#{employeeId}, #{roleId})")
    int insertEmployeeRole(@Param("employeeId") Long employeeId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM employee_roles WHERE employee_id = #{employeeId}")
    int deleteEmployeeRoles(Long employeeId);
}

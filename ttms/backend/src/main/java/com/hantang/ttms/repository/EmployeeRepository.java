package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Employee;

/**
 * 员工（售票员/管理员）数据访问层（MyBatis Mapper）。
 *
 * <p>负责 employees 表的 CRUD 操作，提供按 ID、工号查询，按工号或姓名
 * 模糊搜索，以及工号存在性检查等功能。员工身份包括售票员和管理员，
 * 通过 position 字段区分。</p>
 *
 * @author XUPT
 */
public interface EmployeeRepository {

    /**
     * 查询全部员工列表，按 ID 排序。
     *
     * @return 全部员工列表
     */
    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees ORDER BY id")
    List<Employee> findAll();

    /**
     * 根据员工 ID 查询单个员工。
     *
     * @param id 员工主键 ID
     * @return 员工实体，未找到返回 null
     */
    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE id = #{id}")
    Employee selectById(Long id);

    /**
     * 根据员工 ID 查询单个员工，返回 Optional 包装。
     *
     * @param id 员工主键 ID
     * @return 员工的 Optional 对象
     */
    default Optional<Employee> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 根据工号查询员工（用于登录认证）。
     *
     * @param employeeNo 员工工号
     * @return 员工实体，未找到返回 null
     */
    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE employee_no = #{employeeNo}")
    Employee selectByEmployeeNo(String employeeNo);

    /**
     * 根据工号查询员工，返回 Optional 包装。
     *
     * @param employeeNo 员工工号
     * @return 员工的 Optional 对象
     */
    default Optional<Employee> findByEmployeeNo(String employeeNo) {
        return Optional.ofNullable(selectByEmployeeNo(employeeNo));
    }

    /**
     * 按工号或姓名模糊搜索员工（支持管理端搜索）。
     *
     * <p>工号和姓名均使用 LIKE 模糊匹配。</p>
     *
     * @param employeeNo 工号关键词
     * @param name       姓名关键词
     * @return 匹配的员工列表
     */
    @Select("""
        SELECT id, employee_no, name, position, phone, email, password_hash, status
        FROM employees
        WHERE employee_no LIKE CONCAT('%', #{employeeNo}, '%') OR name LIKE CONCAT('%', #{name}, '%')
        ORDER BY id
        """)
    List<Employee> findByEmployeeNoContainingOrNameContaining(@Param("employeeNo") String employeeNo, @Param("name") String name);

    /**
     * 统计指定工号的员工数量（用于重号检查）。
     *
     * @param employeeNo 员工工号
     * @return 该工号的记录数
     */
    @Select("SELECT COUNT(*) FROM employees WHERE employee_no = #{employeeNo}")
    long countByEmployeeNo(String employeeNo);

    /**
     * 检查指定工号是否已存在。
     *
     * @param employeeNo 员工工号
     * @return true 表示工号已存在
     */
    default boolean existsByEmployeeNo(String employeeNo) {
        return countByEmployeeNo(employeeNo) > 0;
    }

    /**
     * 新增一名员工。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param employee 员工实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO employees (employee_no, name, position, phone, email, password_hash, status, created_at, updated_at)
        VALUES (#{employeeNo}, #{name}, #{position}, #{phone}, #{email}, #{passwordHash}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Employee employee);

    /**
     * 更新员工信息。
     *
     * @param employee 员工实体
     * @return 影响行数
     */
    @Update("""
        UPDATE employees
        SET employee_no = #{employeeNo}, name = #{name}, position = #{position}, phone = #{phone},
            email = #{email}, password_hash = #{passwordHash}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Employee employee);

    /**
     * 保存员工记录（新增或更新）。
     *
     * <p>ID 为空时执行 insert，否则执行 update。</p>
     *
     * @param employee 员工实体
     * @return 保存后的员工实体（含回填的 ID）
     */
    default Employee save(Employee employee) {
        if (employee.getId() == null) {
            insert(employee);
        } else {
            update(employee);
        }
        return employee;
    }
}

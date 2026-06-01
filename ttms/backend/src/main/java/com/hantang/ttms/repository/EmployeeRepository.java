package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Employee;

public interface EmployeeRepository {
    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees ORDER BY id")
    List<Employee> findAll();

    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE id = #{id}")
    Employee selectById(Long id);

    default Optional<Employee> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE employee_no = #{employeeNo}")
    Employee selectByEmployeeNo(String employeeNo);

    default Optional<Employee> findByEmployeeNo(String employeeNo) {
        return Optional.ofNullable(selectByEmployeeNo(employeeNo));
    }

    @Select("""
        SELECT id, employee_no, name, position, phone, email, password_hash, status
        FROM employees
        WHERE employee_no LIKE CONCAT('%', #{employeeNo}, '%') OR name LIKE CONCAT('%', #{name}, '%')
        ORDER BY id
        """)
    List<Employee> findByEmployeeNoContainingOrNameContaining(@Param("employeeNo") String employeeNo, @Param("name") String name);

    @Select("SELECT COUNT(*) FROM employees WHERE employee_no = #{employeeNo}")
    long countByEmployeeNo(String employeeNo);

    default boolean existsByEmployeeNo(String employeeNo) {
        return countByEmployeeNo(employeeNo) > 0;
    }

    @Insert("""
        INSERT INTO employees (employee_no, name, position, phone, email, password_hash, status, created_at, updated_at)
        VALUES (#{employeeNo}, #{name}, #{position}, #{phone}, #{email}, #{passwordHash}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Employee employee);

    @Update("""
        UPDATE employees
        SET employee_no = #{employeeNo}, name = #{name}, position = #{position}, phone = #{phone},
            email = #{email}, password_hash = #{passwordHash}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Employee employee);

    default Employee save(Employee employee) {
        if (employee.getId() == null) {
            insert(employee);
        } else {
            update(employee);
        }
        return employee;
    }
}

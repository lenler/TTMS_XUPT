package com.hantang.ttms.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hantang.ttms.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeNo(String employeeNo);
    List<Employee> findByEmployeeNoContainingOrNameContaining(String employeeNo, String name);
    boolean existsByEmployeeNo(String employeeNo);
}

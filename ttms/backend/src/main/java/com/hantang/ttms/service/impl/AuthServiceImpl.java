package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Employee;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.dto.RegisterCustomerRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.EmployeeRepository;
import com.hantang.ttms.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        String userType = request.userType() == null ? "employee" : request.userType().trim().toLowerCase();
        if ("customer".equals(userType)) {
            Customer customer = customerRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));
            if (customer.getStatus() != Status.ACTIVE || !matches(request.password(), customer.getPasswordHash())) {
                throw new BusinessException("用户名或密码错误");
            }
            return new AuthResponse(
                customer.getId(),
                customer.getUsername(),
                customer.getName(),
                "CUSTOMER",
                List.of("CUSTOMER"),
                List.of("customer:schedule", "customer:order")
            );
        }

        Employee employee = employeeRepository.findByEmployeeNo(request.username())
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));
        if (employee.getStatus() != Status.ACTIVE || !matches(request.password(), employee.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        return new AuthResponse(
            employee.getId(),
            employee.getEmployeeNo(),
            employee.getName(),
            "EMPLOYEE",
            List.of(employee.getPosition()),
            permissionsFor(employee.getPosition())
        );
    }

    @Override
    @Transactional
    public UserResponse registerCustomer(RegisterCustomerRequest request) {
        if (customerRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        Customer customer = new Customer();
        customer.setUsername(request.username());
        customer.setPasswordHash(passwordEncoder.encode(request.password()));
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        return UserServiceImpl.toCustomerResponse(customerRepository.save(customer));
    }

    private boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private List<String> permissionsFor(String position) {
        if (position == null) {
            return List.of();
        }
        return switch (position) {
            case "系统管理员" -> List.of("studio:manage", "play:manage", "user:manage", "finance:view");
            case "运营经理", "计划员" -> List.of("play:manage", "schedule:manage");
            case "售票员" -> List.of("sale:manage", "ticket:view");
            case "会计员", "财务经理" -> List.of("finance:view");
            case "场务员" -> List.of("ticket:check");
            default -> List.of();
        };
    }
}

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

/**
 * 认证业务服务实现
 *
 * 支持管理端（员工）和观众端（客户）的统一登录入口：
 * - 通过 userType 参数区分登录端（employee / customer）
 * - 密码匹配兼容 BCrypt 哈希和明文（开发/测试用）
 * - 登录成功后返回角色、权限列表、菜单权限标识
 *
 * 权限分配规则：
 * - 系统管理员：全部权限
 * - 运营经理/计划员：剧目和排期管理
 * - 售票员：售票和票务查询
 * - 会计员/财务经理：财务查看
 * - 场务员：验票入场
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /** 构造函数注入数据访问层和密码加密器 */
    public AuthServiceImpl(
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     *
     * 实现细节：
     * - 统一使用"用户名或密码错误"的错误信息，防止用户枚举攻击
     * - 支持明文密码的兼容模式（开发/测试阶段使用，生产环境应禁用）
     * - 观众端登录返回 CUSTOMER 角色和对应权限
     */
    @Override
    public AuthResponse login(AuthRequest request) {
        String userType = request.userType() == null ? "employee" : request.userType().trim().toLowerCase();
        // 观众端登录
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

        // 管理端登录
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

    /**
     * {@inheritDoc}
     *
     * 校验用户名唯一性，使用 BCrypt 加密存储密码
     */
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

    /**
     * 密码匹配校验
     *
     * 兼容两种密码存储模式：
     * 1. BCrypt 哈希密码（以 $2a$/$2b$/$2y$ 开头）：使用 BCrypt 匹配
     * 2. 明文密码（开发/测试环境）：直接字符串比较
     *
     * @param rawPassword 用户输入的原始密码
     * @param storedPassword 数据库中存储的密码（哈希或明文）
     * @return true 表示匹配成功
     */
    private boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        // BCrypt 哈希密码格式识别
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // 明文密码兼容模式
        return storedPassword.equals(rawPassword);
    }

    /**
     * 根据员工职位返回对应的权限标识列表
     *
     * @param position 员工职位
     * @return 权限标识列表（如 "studio:manage", "play:manage" 等）
     */
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

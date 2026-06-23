package com.hantang.ttms.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Employee;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.dto.CustomerRequest;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.repository.CustomerRepository;
import com.hantang.ttms.repository.EmployeeRepository;
import com.hantang.ttms.service.UserService;

/**
 * 用户管理业务服务实现
 *
 * 负责管理端的员工和客户 CRUD 操作：
 *
 * 员工管理：
 * - 支持按工号/姓名模糊搜索
 * - 新增时校验工号唯一性
 * - 修改时如更改工号需重新校验唯一性
 * - 密码使用 BCrypt 加密存储，为空时使用默认密码 "123456"
 *
 * 客户管理：
 * - 支持按用户名/姓名模糊搜索
 * - 新增时校验用户名唯一性
 * - 修改时如更改用户名需重新校验唯一性
 *
 * 禁用操作均为逻辑删除（状态设为 DISABLED），保留历史数据
 */
@Service
public class UserServiceImpl implements UserService {
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /** 构造函数注入数据访问层和密码加密器 */
    public UserServiceImpl(
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** {@inheritDoc} */
    @Override
    public List<UserResponse> listEmployees(String keyword) {
        List<Employee> employees = keyword == null || keyword.isBlank()
            ? employeeRepository.findAll()
            : employeeRepository.findByEmployeeNoContainingOrNameContaining(keyword, keyword);
        return employees.stream().map(UserServiceImpl::toEmployeeResponse).toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UserResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeNo(request.employeeNo())) {
            throw new BusinessException("工号已存在");
        }
        Employee employee = new Employee();
        apply(employee, request);
        employee.setPasswordHash(passwordEncoder.encode(resolvePassword(request.password())));
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    /**
     * {@inheritDoc}
     *
     * 修改时支持部分更新：密码为空时不修改密码
     */
    @Override
    @Transactional
    public UserResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("员工不存在"));
        // 检查新工号是否与其他员工冲突
        if (!employee.getEmployeeNo().equals(request.employeeNo())
            && employeeRepository.existsByEmployeeNo(request.employeeNo())) {
            throw new BusinessException("工号已存在");
        }
        apply(employee, request);
        // 仅当传入了非空密码时才更新密码
        if (request.password() != null && !request.password().isBlank()) {
            employee.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    /**
     * {@inheritDoc}
     *
     * 禁用后该员工不可登录管理端
     */
    @Override
    @Transactional
    public void disableEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new BusinessException("员工不存在"));
        employee.setStatus(Status.DISABLED);
        employeeRepository.save(employee);
    }

    /** {@inheritDoc} */
    @Override
    public List<UserResponse> listCustomers(String keyword) {
        List<Customer> customers = keyword == null || keyword.isBlank()
            ? customerRepository.findAll()
            : customerRepository.findByUsernameContainingOrNameContaining(keyword, keyword);
        return customers.stream().map(UserServiceImpl::toCustomerResponse).toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UserResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        Customer customer = new Customer();
        apply(customer, request);
        customer.setPasswordHash(passwordEncoder.encode(resolvePassword(request.password())));
        return toCustomerResponse(customerRepository.save(customer));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public UserResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new BusinessException("顾客不存在"));
        if (!customer.getUsername().equals(request.username())
            && customerRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        apply(customer, request);
        if (request.password() != null && !request.password().isBlank()) {
            customer.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toCustomerResponse(customerRepository.save(customer));
    }

    /**
     * {@inheritDoc}
     *
     * 禁用后该客户不可登录观众端和在线购票
     */
    @Override
    @Transactional
    public void disableCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new BusinessException("顾客不存在"));
        customer.setStatus(Status.DISABLED);
        customerRepository.save(customer);
    }

    /**
     * 将 Employee 实体转换为员工用户响应 DTO
     * 包级私有方法，供 AuthServiceImpl 注册时复用
     */
    static UserResponse toEmployeeResponse(Employee employee) {
        return new UserResponse(
            employee.getId(),
            null,
            employee.getEmployeeNo(),
            employee.getName(),
            employee.getPosition(),
            employee.getPhone(),
            employee.getEmail(),
            null,
            null,
            employee.getStatus()
        );
    }

    /**
     * 将 Customer 实体转换为客户用户响应 DTO
     * 包级私有方法，供 AuthServiceImpl 注册时复用
     */
    static UserResponse toCustomerResponse(Customer customer) {
        return new UserResponse(
            customer.getId(),
            customer.getUsername(),
            null,
            customer.getName(),
            null,
            customer.getPhone(),
            customer.getEmail(),
            customer.getGender(),
            customer.getBalance(),
            customer.getStatus()
        );
    }

    /** 将 EmployeeRequest 的字段值赋给 Employee 实体 */
    private void apply(Employee employee, EmployeeRequest request) {
        employee.setEmployeeNo(request.employeeNo());
        employee.setName(request.name());
        employee.setPosition(request.position());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
    }

    /** 将 CustomerRequest 的字段值赋给 Customer 实体 */
    private void apply(Customer customer, CustomerRequest request) {
        customer.setUsername(request.username());
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setBalance(request.balance() == null ? BigDecimal.ZERO : request.balance());
    }

    /**
     * 解析密码：请求中的密码为空或空白时返回默认密码
     * @param password 请求中的密码字段
     * @return 最终使用的密码（原始密码或默认密码 "123456"）
     */
    private String resolvePassword(String password) {
        return password == null || password.isBlank() ? "123456" : password;
    }
}

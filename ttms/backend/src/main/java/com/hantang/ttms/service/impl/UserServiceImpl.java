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

@Service
public class UserServiceImpl implements UserService {
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
        EmployeeRepository employeeRepository,
        CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> listEmployees(String keyword) {
        List<Employee> employees = keyword == null || keyword.isBlank()
            ? employeeRepository.findAll()
            : employeeRepository.findByEmployeeNoContainingOrNameContaining(keyword, keyword);
        return employees.stream().map(UserServiceImpl::toEmployeeResponse).toList();
    }

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

    @Override
    @Transactional
    public UserResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new BusinessException("员工不存在"));
        if (!employee.getEmployeeNo().equals(request.employeeNo()) && employeeRepository.existsByEmployeeNo(request.employeeNo())) {
            throw new BusinessException("工号已存在");
        }
        apply(employee, request);
        if (request.password() != null && !request.password().isBlank()) {
            employee.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void disableEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new BusinessException("员工不存在"));
        employee.setStatus(Status.DISABLED);
        employeeRepository.save(employee);
    }

    @Override
    public List<UserResponse> listCustomers(String keyword) {
        List<Customer> customers = keyword == null || keyword.isBlank()
            ? customerRepository.findAll()
            : customerRepository.findByUsernameContainingOrNameContaining(keyword, keyword);
        return customers.stream().map(UserServiceImpl::toCustomerResponse).toList();
    }

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

    @Override
    @Transactional
    public UserResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new BusinessException("顾客不存在"));
        if (!customer.getUsername().equals(request.username()) && customerRepository.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }
        apply(customer, request);
        if (request.password() != null && !request.password().isBlank()) {
            customer.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toCustomerResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void disableCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new BusinessException("顾客不存在"));
        customer.setStatus(Status.DISABLED);
        customerRepository.save(customer);
    }

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

    private void apply(Employee employee, EmployeeRequest request) {
        employee.setEmployeeNo(request.employeeNo());
        employee.setName(request.name());
        employee.setPosition(request.position());
        employee.setPhone(request.phone());
        employee.setEmail(request.email());
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setUsername(request.username());
        customer.setName(request.name());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setBalance(request.balance() == null ? BigDecimal.ZERO : request.balance());
    }

    private String resolvePassword(String password) {
        return password == null || password.isBlank() ? "123456" : password;
    }
}

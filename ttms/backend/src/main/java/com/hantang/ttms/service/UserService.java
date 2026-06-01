package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.dto.CustomerRequest;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.UserResponse;

public interface UserService {
    List<UserResponse> listEmployees(String keyword);
    UserResponse createEmployee(EmployeeRequest request);
    UserResponse updateEmployee(Long id, EmployeeRequest request);
    void disableEmployee(Long id);

    List<UserResponse> listCustomers(String keyword);
    UserResponse createCustomer(CustomerRequest request);
    UserResponse updateCustomer(Long id, CustomerRequest request);
    void disableCustomer(Long id);
}

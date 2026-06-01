package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.CustomerRequest;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/employees")
    public ApiResponse<List<UserResponse>> listEmployees(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.listEmployees(keyword));
    }

    @PostMapping("/employees")
    public ApiResponse<UserResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(userService.createEmployee(request));
    }

    @PutMapping("/employees/{id}")
    public ApiResponse<UserResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(userService.updateEmployee(id, request));
    }

    @DeleteMapping("/employees/{id}")
    public ApiResponse<Void> disableEmployee(@PathVariable Long id) {
        userService.disableEmployee(id);
        return ApiResponse.ok();
    }

    @GetMapping("/customers")
    public ApiResponse<List<UserResponse>> listCustomers(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.listCustomers(keyword));
    }

    @PostMapping("/customers")
    public ApiResponse<UserResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok(userService.createCustomer(request));
    }

    @PutMapping("/customers/{id}")
    public ApiResponse<UserResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok(userService.updateCustomer(id, request));
    }

    @DeleteMapping("/customers/{id}")
    public ApiResponse<Void> disableCustomer(@PathVariable Long id) {
        userService.disableCustomer(id);
        return ApiResponse.ok();
    }
}

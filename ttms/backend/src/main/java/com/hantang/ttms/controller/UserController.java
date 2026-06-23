package com.hantang.ttms.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.CustomerRequest;
import com.hantang.ttms.dto.EmployeeRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.UserService;

/**
 * 用户管理控制器，提供员工（售票员）和观众的增删改查功能。
 *
 * <p>基础路径：{@code /users}</p>
 * <ul>
 *   <li>GET /users/employees —— 查询员工列表（可按关键字搜索）</li>
 *   <li>POST /users/employees —— 创建新员工</li>
 *   <li>PUT /users/employees/{id} —— 修改员工信息</li>
 *   <li>DELETE /users/employees/{id} —— 禁用员工账号</li>
 *   <li>GET /users/customers —— 查询观众列表（可按关键字搜索）</li>
 *   <li>POST /users/customers —— 创建新观众</li>
 *   <li>PUT /users/customers/{id} —— 修改观众信息</li>
 *   <li>DELETE /users/customers/{id} —— 禁用观众账号</li>
 * </ul>
 */
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * 构造用户控制器，注入用户服务。
     *
     * @param userService 用户服务
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询员工列表，支持按关键字模糊搜索。
     *
     * <p>GET /users/employees</p>
     *
     * @param keyword 搜索关键字，可选，用于按姓名或账号模糊匹配
     * @return 员工用户列表
     */
    @GetMapping("/employees")
    public ApiResponse<List<UserResponse>> listEmployees(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.listEmployees(keyword));
    }

    /**
     * 创建新员工（售票员）账号。
     *
     * <p>POST /users/employees</p>
     *
     * @param request 员工创建请求体，包含账号、密码、姓名、电话等信息
     * @return 创建成功的员工用户信息
     */
    @PostMapping("/employees")
    public ApiResponse<UserResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(userService.createEmployee(request));
    }

    /**
     * 修改员工信息。
     *
     * <p>PUT /users/employees/{id}</p>
     *
     * @param id      员工 ID（路径变量）
     * @param request 员工修改请求体，包含要更新的字段
     * @return 更新后的员工用户信息
     */
    @PutMapping("/employees/{id}")
    public ApiResponse<UserResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.ok(userService.updateEmployee(id, request));
    }

    /**
     * 禁用员工账号（逻辑删除）。
     *
     * <p>DELETE /users/employees/{id}</p>
     *
     * @param id 员工 ID（路径变量）
     * @return 空响应，表示操作成功
     */
    @DeleteMapping("/employees/{id}")
    public ApiResponse<Void> disableEmployee(@PathVariable Long id) {
        userService.disableEmployee(id);
        return ApiResponse.ok();
    }

    /**
     * 查询观众列表，支持按关键字模糊搜索。
     *
     * <p>GET /users/customers</p>
     *
     * @param keyword 搜索关键字，可选，用于按姓名或账号模糊匹配
     * @return 观众用户列表
     */
    @GetMapping("/customers")
    public ApiResponse<List<UserResponse>> listCustomers(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(userService.listCustomers(keyword));
    }

    /**
     * 创建新观众账号（由管理员后台创建）。
     *
     * <p>POST /users/customers</p>
     *
     * @param request 观众创建请求体，包含账号、密码、姓名、电话等信息
     * @return 创建成功的观众用户信息
     */
    @PostMapping("/customers")
    public ApiResponse<UserResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok(userService.createCustomer(request));
    }

    /**
     * 修改观众信息。
     *
     * <p>PUT /users/customers/{id}</p>
     *
     * @param id      观众 ID（路径变量）
     * @param request 观众修改请求体，包含要更新的字段
     * @return 更新后的观众用户信息
     */
    @PutMapping("/customers/{id}")
    public ApiResponse<UserResponse> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return ApiResponse.ok(userService.updateCustomer(id, request));
    }

    /**
     * 禁用观众账号（逻辑删除）。
     *
     * <p>DELETE /users/customers/{id}</p>
     *
     * @param id 观众 ID（路径变量）
     * @return 空响应，表示操作成功
     */
    @DeleteMapping("/customers/{id}")
    public ApiResponse<Void> disableCustomer(@PathVariable Long id) {
        userService.disableCustomer(id);
        return ApiResponse.ok();
    }
}

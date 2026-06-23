package com.hantang.ttms.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.dto.RegisterCustomerRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.AuthService;

/**
 * 认证控制器，处理用户登录和观众自助注册请求。
 *
 * <p>基础路径：{@code /auth}</p>
 * <ul>
 *   <li>POST /auth/login —— 用户登录</li>
 *   <li>POST /auth/customers/register —— 观众自助注册</li>
 * </ul>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * 构造认证控制器，注入认证服务。
     *
     * @param authService 认证服务
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录。
     *
     * <p>POST /auth/login</p>
     *
     * @param request 登录请求体，包含账号（account）和密码（password）
     * @return 认证响应，包含 JWT 令牌和用户基本信息
     */
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    /**
     * 观众自助注册。
     *
     * <p>POST /auth/customers/register</p>
     *
     * @param request 注册请求体，包含账号、密码、姓名、电话等信息
     * @return 注册成功的用户信息
     */
    @PostMapping("/customers/register")
    public ApiResponse<UserResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        return ApiResponse.ok(authService.registerCustomer(request));
    }
}

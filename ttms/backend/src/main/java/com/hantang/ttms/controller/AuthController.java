package com.hantang.ttms.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.hantang.ttms.common.ApiResponse;
import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.dto.RegisterCustomerRequest;
import com.hantang.ttms.dto.UserResponse;
import com.hantang.ttms.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/customers/register")
    public ApiResponse<UserResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request) {
        return ApiResponse.ok(authService.registerCustomer(request));
    }
}

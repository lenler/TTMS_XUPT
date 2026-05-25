package com.hantang.ttms.service;

import com.hantang.ttms.dto.AuthRequest;
import com.hantang.ttms.dto.AuthResponse;
import com.hantang.ttms.dto.RegisterCustomerRequest;
import com.hantang.ttms.dto.UserResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    UserResponse registerCustomer(RegisterCustomerRequest request);
}

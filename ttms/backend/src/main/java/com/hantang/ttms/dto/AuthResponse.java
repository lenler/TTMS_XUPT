package com.hantang.ttms.dto;

import java.util.List;

public record AuthResponse(
    Long id,
    String username,
    String name,
    String userType,
    List<String> roles,
    List<String> permissions
) {}

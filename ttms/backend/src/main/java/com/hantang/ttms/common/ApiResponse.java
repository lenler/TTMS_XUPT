package com.hantang.ttms.common;

public record ApiResponse<T>(String code, String message, T data) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("10000", "请求成功", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>("10000", "请求成功", null);
    }

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

package com.hantang.ttms.dto;

public record AdminApiResponse<T>(String resCode, String resMsg, T data) {
    public static <T> AdminApiResponse<T> ok(T data) {
        return new AdminApiResponse<>("10000", "请求成功", data);
    }
}

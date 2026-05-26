package com.ttms.common;

/**
 * 前后端统一响应结构。
 *
 * @param resCode 业务响应码
 * @param resMsg 响应说明
 * @param data 响应数据
 * @param <T> 响应数据类型
 */
public record ApiResponse<T>(String resCode, String resMsg, T data) {

    /**
     * 构造成功响应。
     *
     * @param data 响应数据
     * @param <T> 响应数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.code(), ErrorCode.SUCCESS.message(), data);
    }

    /**
     * 构造失败响应。
     *
     * @param errorCode 错误码枚举
     * @param message 错误说明
     * @return 失败响应对象
     */
    public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.code(), message, null);
    }
}

package com.hantang.ttms.common;

/**
 * 统一 API 响应体（不可变记录类）。
 *
 * <p>所有 Controller 接口均使用此类作为返回值包装，确保前端收到的响应格式一致。
 * 响应格式为 {@code { "code": "10000", "message": "请求成功", "data": {...} }}。</p>
 *
 * <h3>状态码约定</h3>
 * <ul>
 *   <li>{@code 10000} — 请求成功</li>
 *   <li>{@code 40000} — 通用业务异常</li>
 *   <li>{@code 40001} — 请求参数不合法</li>
 *   <li>其他以 {@code 4} 开头的自定义错误码</li>
 * </ul>
 *
 * @param <T>     响应数据的具体类型
 * @param code    业务状态码，字符串格式
 * @param message 提示信息
 * @param data    响应数据，无数据时为 null
 * @author XUPT
 */
public record ApiResponse<T>(String code, String message, T data) {

    /**
     * 构建成功的响应体（含数据）。
     *
     * @param <T>  数据类型
     * @param data 响应数据
     * @return ApiResponse 实例，状态码 10000
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("10000", "请求成功", data);
    }

    /**
     * 构建成功的响应体（无数据）。
     *
     * @return ApiResponse 实例，状态码 10000，data 为 null
     */
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>("10000", "请求成功", null);
    }

    /**
     * 构建失败的响应体。
     *
     * @param code    业务错误码
     * @param message 错误提示信息
     * @return ApiResponse 实例，data 为 null
     */
    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}

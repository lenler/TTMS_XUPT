package com.hantang.ttms.common;

/**
 * 业务异常类。
 *
 * <p>用于在 Service 层抛出可预期的业务逻辑异常，由
 * {@link GlobalExceptionHandler} 统一捕获并转换为 {@link ApiResponse} 格式返回前端。</p>
 *
 * <p>支持两种构造方式：</p>
 * <ul>
 *   <li>仅传入消息 — 默认错误码 {@code 40000}</li>
 *   <li>传入自定义错误码 + 消息</li>
 * </ul>
 *
 * @author XUPT
 */
public class BusinessException extends RuntimeException {

    /** 业务错误码 */
    private final String code;

    /**
     * 使用默认错误码 40000 构造业务异常。
     *
     * @param message 错误提示信息
     */
    public BusinessException(String message) {
        this("40000", message);
    }

    /**
     * 使用自定义错误码构造业务异常。
     *
     * @param code    业务错误码
     * @param message 错误提示信息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务错误码。
     *
     * @return 错误码字符串
     */
    public String getCode() {
        return code;
    }
}

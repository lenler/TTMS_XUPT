package com.ttms.common;

/**
 * 表示业务规则或数据状态不满足要求的异常。
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 创建业务异常。
     *
     * @param errorCode 业务错误码
     * @param message 错误说明
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 获取业务错误码。
     *
     * @return 业务错误码
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

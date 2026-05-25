package com.hantang.ttms.common;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String message) {
        this("40000", message);
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

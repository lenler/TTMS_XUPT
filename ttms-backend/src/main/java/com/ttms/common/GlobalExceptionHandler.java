package com.ttms.common;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

/**
 * 将后端异常统一转换为接口文档约定的响应格式。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * @param exception 业务异常
     * @return 统一失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.failure(exception.getErrorCode(), exception.getMessage());
    }

    /**
     * 处理请求体验证失败。
     *
     * @param exception 参数校验异常
     * @return 参数错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + error.getDefaultMessage())
                .orElse(ErrorCode.PARAM_ERROR.message());
        return ApiResponse.failure(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 处理查询参数绑定失败。
     *
     * @param exception 参数绑定异常
     * @return 参数错误响应
     */
    @ExceptionHandler({BindException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ApiResponse<Void> handleBadRequest(Exception exception) {
        return ApiResponse.failure(ErrorCode.PARAM_ERROR, ErrorCode.PARAM_ERROR.message());
    }

    /**
     * 处理查询参数校验失败。
     *
     * @param exception 参数约束异常
     * @return 参数错误响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException exception) {
        return ApiResponse.failure(ErrorCode.PARAM_ERROR, ErrorCode.PARAM_ERROR.message());
    }

    /**
     * 处理未预期的服务端异常。
     *
     * @param exception 服务端异常
     * @return 服务端错误响应
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        return ApiResponse.failure(ErrorCode.SERVER_ERROR, ErrorCode.SERVER_ERROR.message());
    }
}

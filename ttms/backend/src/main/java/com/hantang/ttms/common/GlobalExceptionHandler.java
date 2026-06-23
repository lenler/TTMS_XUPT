package com.hantang.ttms.common;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * <p>使用 {@code @RestControllerAdvice} 拦截所有 Controller 层抛出的异常，
 * 将其统一转换为 {@link ApiResponse} 格式返回，确保前端始终收到结构一致的响应体。</p>
 *
 * <h3>处理的异常类型</h3>
 * <ul>
 *   <li>{@link BusinessException} — 业务逻辑异常，返回错误码 40000 或自定义码，
 *       HTTP 状态码 400</li>
 *   <li>{@link MethodArgumentNotValidException} / {@link BindException} —
 *       请求参数校验失败，统一返回错误码 40001，HTTP 状态码 400</li>
 * </ul>
 *
 * @author XUPT
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     *
     * <p>从异常中提取业务错误码和消息，包装为 ApiResponse.fail 返回。</p>
     *
     * @param ex 业务异常实例
     * @return 失败响应体，HTTP 400
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理请求参数校验失败异常（@Valid 校验失败或数据绑定异常）。
     *
     * <p>统一返回错误码 40001，不暴露具体的校验细节。</p>
     *
     * @param ex 校验异常实例
     * @return 失败响应体，HTTP 400
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(Exception ex) {
        return ApiResponse.fail("40001", "请求参数不合法");
    }
}

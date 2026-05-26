package com.ttms.common;

/**
 * 接口设计文档定义的统一响应码。
 *
 * @param code 响应码
 * @param message 默认响应说明
 */
public record ErrorCode(String code, String message) {

    public static final ErrorCode SUCCESS = new ErrorCode("10000", "请求成功");
    public static final ErrorCode PARAM_ERROR = new ErrorCode("20001", "参数错误");
    public static final ErrorCode UNAUTHORIZED = new ErrorCode("20002", "未登录");
    public static final ErrorCode FORBIDDEN = new ErrorCode("20003", "无权限");
    public static final ErrorCode NOT_FOUND = new ErrorCode("20004", "数据不存在");
    public static final ErrorCode BUSINESS_LIMIT = new ErrorCode("20005", "业务规则限制");
    public static final ErrorCode SERVER_ERROR = new ErrorCode("30001", "服务器内部错误");
}

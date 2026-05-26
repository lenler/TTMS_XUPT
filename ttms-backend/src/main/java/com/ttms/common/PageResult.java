package com.ttms.common;

import java.util.List;

/**
 * 列表接口统一分页响应结构。
 *
 * @param list 当前页数据
 * @param total 总记录数
 * @param page 当前页码
 * @param pageSize 每页数量
 * @param <T> 列表元素类型
 */
public record PageResult<T>(List<T> list, long total, int page, int pageSize) {
}

package com.hantang.ttms.common;

import java.util.List;

/**
 * 分页查询结果（不可变记录类）。
 *
 * <p>封装分页查询的返回数据，包含当前页记录列表、总记录数、页码和每页大小。
 * 前端可根据 total、page、size 计算总页数并渲染分页组件。</p>
 *
 * <p>响应示例：{@code { "records": [...], "total": 100, "page": 1, "size": 10 }}</p>
 *
 * @param <T>     记录数据类型
 * @param records 当前页的记录列表
 * @param total   符合条件的总记录数
 * @param page    当前页码（从 1 开始）
 * @param size    每页记录数
 * @author XUPT
 */
public record PageResult<T>(
    List<T> records,
    long total,
    int page,
    int size
) {
    /**
     * 构建分页结果对象。
     *
     * @param <T>     记录数据类型
     * @param records 当前页的记录列表
     * @param total   符合条件的总记录数
     * @param page    当前页码
     * @param size    每页记录数
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        return new PageResult<>(records, total, page, size);
    }
}

package com.hantang.ttms.common;

import java.util.List;

public record PageResult<T>(
    List<T> records,
    long total,
    int page,
    int size
) {
    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        return new PageResult<>(records, total, page, size);
    }
}

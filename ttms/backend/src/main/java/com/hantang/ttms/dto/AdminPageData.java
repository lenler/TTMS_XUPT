package com.hantang.ttms.dto;

import java.util.List;

public record AdminPageData<T>(List<T> list, long total, int page, int pageSize) {
    public static <T> AdminPageData<T> of(List<T> list, int page, int pageSize) {
        return new AdminPageData<>(list, list.size(), page, pageSize);
    }
}

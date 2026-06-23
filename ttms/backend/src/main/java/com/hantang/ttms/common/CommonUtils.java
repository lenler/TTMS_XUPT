package com.hantang.ttms.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * 通用工具类 —— 提供常用的判空、格式化等辅助方法
 *
 * @author lyd60417
 */
public class CommonUtils {

    private CommonUtils() {
        // 工具类不允许实例化
    }

    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 判断 Map 是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 格式化当前时间为指定格式
     */
    public static String formatNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 默认日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static String formatNow() {
        return formatNow("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 安全截取字符串，避免越界
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() <= maxLength ? str : str.substring(0, maxLength);
    }
}

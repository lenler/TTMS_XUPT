package com.hantang.ttms.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间工具类 —— 提供常用的日期格式化、计算和转换方法
 *
 * @author lyd
 */
public class DateUtils {

    private DateUtils() {}

    /** 默认日期格式 */
    public static final String DEFAULT_DATE = "yyyy-MM-dd";
    /** 默认时间格式 */
    public static final String DEFAULT_TIME = "HH:mm:ss";
    /** 默认日期时间格式 */
    public static final String DEFAULT_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式化当前日期
     */
    public static String today() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATE));
    }

    /**
     * 格式化当前日期时间
     */
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME));
    }

    /**
     * 格式化指定日期时间
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * 判断是否在有效期内（起止日期包含当天）
     */
    public static boolean isWithinRange(LocalDate start, LocalDate end) {
        LocalDate now = LocalDate.now();
        return (start == null || !now.isBefore(start)) && (end == null || !now.isAfter(end));
    }
}

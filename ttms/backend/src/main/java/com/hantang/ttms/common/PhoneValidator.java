package com.hantang.ttms.common;

import java.util.regex.Pattern;

/**
 * 手机号校验工具类
 *
 * @author lyd
 */
public class PhoneValidator {

    private PhoneValidator() {}

    // 中国大陆手机号：1开头的11位数字
    private static final Pattern CN_PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 校验中国大陆手机号格式
     */
    public static boolean isValidCnPhone(String phone) {
        return phone != null && CN_PHONE.matcher(phone.trim()).matches();
    }

    /**
     * 脱敏处理：188****1234
     */
    public static String mask(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}

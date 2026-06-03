package com.hantang.ttms.common;

import java.util.regex.Pattern;

/**
 * 密码校验工具类 —— 提供密码强度验证和常用校验规则
 *
 * @author lyd
 */
public class PasswordValidator {

    private PasswordValidator() {}

    // 最少8位，至少1个大写字母、1个小写字母、1个数字
    private static final Pattern STRONG = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    // 最少6位，至少1个字母和1个数字
    private static final Pattern MEDIUM = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");
    // 非空且至少6位
    private static final Pattern WEAK = Pattern.compile("^.{6,}$");

    public enum Strength { WEAK, MEDIUM, STRONG }

    /**
     * 评估密码强度
     */
    public static Strength evaluate(String password) {
        if (password == null) return Strength.WEAK;
        if (STRONG.matcher(password).matches()) return Strength.STRONG;
        if (MEDIUM.matcher(password).matches()) return Strength.MEDIUM;
        return Strength.WEAK;
    }

    /**
     * 是否为强密码
     */
    public static boolean isStrong(String password) {
        return evaluate(password) == Strength.STRONG;
    }

    /**
     * 是否为合格密码（至少中等强度）
     */
    public static boolean isValid(String password) {
        return evaluate(password).ordinal() >= Strength.MEDIUM.ordinal();
    }

    /**
     * 获取密码校验提示信息
     */
    public static String getHint() {
        return "密码至少6位，建议包含字母和数字";
    }
}

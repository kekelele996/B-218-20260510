package com.petadoption.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 密码验证
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 密码强度校验
     * 规则: 6-20位，至少包含字母和数字
     */
    public static boolean validateStrength(String password) {
        if (password == null || password.length() < 6 || password.length() > 20) {
            return false;
        }
        return password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$");
    }
}

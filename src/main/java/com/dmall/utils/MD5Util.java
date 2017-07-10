package com.dmall.utils;

import org.springframework.util.DigestUtils;

public class MD5Util {
    private static String salt = PropertiesUtil.getProperty("md5.salt");

    public static String getMD5(String origin) {
        String base = origin + salt;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }

}

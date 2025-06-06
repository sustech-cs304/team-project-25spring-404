package com.training.util;

import java.security.MessageDigest;

/**
 * MD5工具类
 */
public class MD5Utils {

    private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f" };

    /**
     * MD5加密
     * 
     * @param source 原字符串
     * @return 加密后的字符串
     */
    public static String encode(String source) {
        String result = null;
        try {
            result = source;
            // 获得MD5摘要对象
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要信息
            messageDigest.update(result.getBytes("UTF-8"));
            // 获得密文
            byte[] resultByte = messageDigest.digest();
            // 把密文转换成十六进制的字符串形式
            result = byteArrayToHexString(resultByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 转换字节数组为十六进制字符串
     * 
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(byteToHexString(b));
        }
        return stringBuilder.toString();
    }

    /**
     * 转换字节为十六进制字符串
     * 
     * @param b 字节
     * @return 十六进制字符串
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
} 
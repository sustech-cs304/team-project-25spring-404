package com.training.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * Cookie工具类
 */
public class CookieUtils {

    /**
     * 设置Cookie
     * 
     * @param response 响应
     * @param name     Cookie名称
     * @param value    Cookie值
     * @param maxAge   Cookie生存时间（秒）
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        setCookie(response, name, value, null, "/", maxAge, false);
    }

    /**
     * 设置Cookie
     * 
     * @param response 响应
     * @param name     Cookie名称
     * @param value    Cookie值
     * @param domain   Cookie域名
     * @param path     Cookie路径
     * @param maxAge   Cookie生存时间（秒）
     * @param isHttpOnly 是否为HttpOnly
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String domain, String path, int maxAge, boolean isHttpOnly) {
        try {
            if (StringUtils.isNotBlank(value)) {
                value = URLEncoder.encode(value, "UTF-8");
            }
            Cookie cookie = new Cookie(name, value);
            if (StringUtils.isNotBlank(domain)) {
                cookie.setDomain(domain);
            }
            cookie.setPath(path);
            cookie.setMaxAge(maxAge);
            cookie.setHttpOnly(isHttpOnly);
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Cookie
     * 
     * @param request 请求
     * @param name    Cookie名称
     * @return Cookie值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 删除Cookie
     * 
     * @param response 响应
     * @param name     Cookie名称
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        setCookie(response, name, "", -1);
    }
} 
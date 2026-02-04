package com.nowcoder.community.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtil {

    /**
     * 从请求中获取指定名称的Cookie值
     * @param request HttpServletRequest对象
     * @param name Cookie名称
     * @return Cookie值，如果不存在返回null
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取指定名称的Cookie对象
     * @param request HttpServletRequest对象
     * @param name Cookie名称
     * @return Cookie对象，如果不存在返回null
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /**
     * 创建Cookie
     * @param name Cookie名称
     * @param value Cookie值
     * @param maxAge 有效期（秒）
     * @return 创建的Cookie对象
     */
    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        // 生产环境应该设置为true，只在HTTPS下传输
        // cookie.setSecure(true);
        // 防止XSS攻击，限制JavaScript访问Cookie
        cookie.setHttpOnly(true);
        return cookie;
    }

    /**
     * 删除Cookie（通过设置maxAge为0）
     * @param name Cookie名称
     * @return 用于删除的Cookie对象
     */
    public static Cookie deleteCookie(String name) {
        return createCookie(name, null, 0);
    }
}

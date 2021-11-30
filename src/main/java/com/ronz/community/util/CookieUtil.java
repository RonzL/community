package com.ronz.community.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/13 21:30
 * @Version 1.0
 */

@Component
public class CookieUtils {

    /**
     * 获取请求中的 cookie 中的凭证信息
     * @param request
     * @return java.lang.String
     */
    public String getTicketValue(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if ("ticket".equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

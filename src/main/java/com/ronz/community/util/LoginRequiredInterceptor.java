package com.ronz.community.util;

import com.ronz.community.annotation.LoginRequired;
import com.ronz.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 自定义拦截器 —— 检查用户登录状态，不允许未登录用户访问某些方法
 * @Author Ronz
 * @Date 2021/5/17 9:38
 * @Version 1.0
 */

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    /**
     * preHandler 在处理器映射器找到对应方法之后，处理器适配器调用对应方法之前调用
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 先检查是不是处理器方法
        if (handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired loginRequired = handlerMethod.getMethodAnnotation(LoginRequired.class);   // 获取 loginRequired 注解
            if (loginRequired != null){
                // 如果注解非空，说明该方法需要检查用户登录状态
                User user = hostHolder.get();
                if (user == null){
                    // user 为空，说明没有用户登录，则不能访问该方法
                    response.sendRedirect(request.getContextPath() + "/index");
                    return false;   // 返回 false，表示不允许放行
                }
            }
        }
        return true;
    }
}

package com.ronz.community.util;

import com.ronz.community.dao.LoginTicketMapper;
import com.ronz.community.entity.LoginTicket;
import com.ronz.community.entity.User;
import com.ronz.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/13 20:24
 * @Version 1.0
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private CookieUtils cookieUtils;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    /**
     * 在处理器映射器找到对应的处理器之后，处理器适配器调用对应的处理器方法之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求中的凭证信息
        String ticket = cookieUtils.getTicketValue(request);
        if (!StringUtils.isBlank(ticket)){
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 判断凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                // 凭证有效
                User user = userService.findUserById(loginTicket.getUserId());
                // 首先判断用户是否有效
                if (user != null && user.getStatus() != 0){
                    // 设置当前线程持有用户
                    hostHolder.set(user);
                }
            }
        }

        // 这个前置处理主要目的就是为了取出 cookie 中的 ticket 代表的 user，取没取出来无所谓的
        // 前面无论是什么情况，这里一定要放行！！！
        return true;
    }

    /**
     * 在处理器适配器调用处理器方法之后，前端控制器渲染视图之前调用
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.get();
        if (user != null && modelAndView != null){
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在请求处理完成之后回调
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}

package com.ronz.community.config;

import com.ronz.community.util.LoginRequiredInterceptor;
import com.ronz.community.util.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description 拦截器配置
 * 为什么拦截器不能直接作为一个组件呢？为什么需要在配置类中进行配置呢？
 * 如果将其配置为一个组件的话，我们主要的目的就是让其可以注册到 IOC 容器中，然后手动调用它
 * 而将其作为一个配置类，SpringBoot 在启动的时候，会自动加载配置类，将拦截器注册起来(并不是注册到IOC容器中)
 * 注册完毕之后，每当有一个请求过来，就知道使用拦截器进行拦截了。
 * @Author Ronz
 * @Date 2021/5/13 20:29
 * @Version 1.0
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginTicketInterceptor loginTicketInterceptor;
    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                // 可以查看 org.springframework.web.util.pattern.PathPattern 类找到 patterns 的规则
                .excludePathPatterns("/css/", "/img/", "/js/");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/css/", "/img/", "/js/");
    }
}

package com.ronz.community.controller.advice;

import com.ronz.community.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/20 19:10
 * @Version 1.0
 */

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    /**
     * ExceptionHandler 注解修饰的方法会在 Controller 方法出现指定异常后被调用
     * 用于处理捕获后的异常。
     */
    @ExceptionHandler(value = Exception.class)
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" +  e.getMessage());
        // 详细打印出现异常时候的栈信息
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            logger.error(stackTraceElement.toString());
        }

        // 返回响应给浏览器
        // 此次请求可能是异步请求也可能是普通请求，需要判断

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){
            // 如果是异步请求
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write(CommonUtils.getJSONString(500, "服务器发生异常！"));
        }else{
            // 如果是普通请求
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}

package com.ronz.community.controller;

import com.google.code.kaptcha.Producer;
import com.ronz.community.annotation.LoginRequired;
import com.ronz.community.dao.LoginTicketMapper;
import com.ronz.community.entity.LoginTicket;
import com.ronz.community.entity.User;
import com.ronz.community.service.UserService;
import com.ronz.community.util.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/11 21:26
 * @Version 1.0
 */

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private Producer producer;
    @Value("${server.servlet.context-path}")
    private String contextPath;


    /**
     * 跳转到注册页面
     */
    @GetMapping(path = "/register")
    public String register(){
        return "site/register";
    }

    /**
     * 注册用户方法
     */
    @PostMapping(path = "/register")
    public String register(Model model, User user){
        Map<String, String> map = userService.registerUser(user);
        if (map == null || map.isEmpty()){   // map 为空，说明注册成功了
            // 传递给前端注册成功信息
            model.addAttribute("msg", "注册成功！我们已经向您的邮箱发送了一封激活邮件，请尽快点击激活。");
            model.addAttribute("target", "/index"); // 将来要跳转到主页
            return "/site/operate-result";
        }
        // 否则，说明注册失败了
        // 传递给前端错误信息
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("emailMsg", map.get("emailMsg"));
        return "site/register";
    }

    /**
     * 激活用户方法
     */
    @GetMapping(path = "/active/{id}/{code}")
    public String active(Model model, @PathVariable("id") int id, @PathVariable("code") String code){
        CommonConstant result = userService.active(id, code);

        model.addAttribute("msg", result.getMsg());
        model.addAttribute("target", "/login");

        return "site/operate-result";
    }


    /**
     * 跳转到登录页面
     *
     * 按照一般思维的来，验证码的生成工作可能是放在 /login 请求中，但是这样是不对的。
     * 仔细想一下，在登录页面的验证码并不是一成不变的，验证码是可以手动刷新的。
     * 如果把验证码的生成放在 /login 中，那么每点击一次 “刷新验证码” 就需要访问一次 /login 页面
     * 这样是不对的，所以需要把验证码的生成放在一个单独的控制器方法中。
     */
    @GetMapping("/login")
    public String login(){
        return "site/login";
    }


    /**
     * 生成验证码
     *
     * 前端必然是 ajax 请求，所以需要使用 ResponseBody
     * @param session 需要将验证码保留在服务器一份，所以需要使用 session
     * @param response 将验证码响应回去
     */
    @GetMapping("/kaptcha")
    // @ResponseBody 注解的作用就是找到对应的处理器
    // 然后改处理器把返回的数据转换为指定格式后写入到 response 的 body 中
    // 其作用相当于是 response.getWriter().write()
    //@ResponseBody
    public void getKaptcha(HttpSession session, HttpServletResponse response){
        String text = producer.createText();    // 生成随机字符
        BufferedImage image = producer.createImage(text);   // 根据字符生成图片

        // 服务器也要保留一份验证码
        session.setAttribute("code", text);
        // 设置响应类型为 png 图片
        response.setContentType("image/png");
        try {
            // 将图片响应回去
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("获取验证码失败：" + e.getMessage());
        }
    }


    /**
     * 登录方法
     * @param username 账号
     * @param password 密码
     * @param code 验证码
     * @param rememberMe 是否记住我
     * @param model 存储返回属性
     * @param session 获取服务端存储的验证码，用于验证
     * @param response 响应 cookie 给客户端
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response){
        // 验证码存在 session 中，所以选择在控制层方法进行验证码判断
        // 首先判断验证码是否符合
        String sessionCode = (String) session.getAttribute("code");
        if (StringUtils.isBlank(sessionCode) || StringUtils.isBlank(code) || !sessionCode.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "验证码错误！");
            return "site/login";
        }

        int expiredSeconds = rememberMe ?
                CommonConstant.REMEMBER_EXPIRED_SECONDS.getCode() :
                CommonConstant.DEFAULT_EXPIRED_SECONDS.getCode();
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        // 判断是否有错误
        if (map.containsKey("usernameMsg")){
            String usernameMsg = (String) map.get("usernameMsg");
            model.addAttribute("usernameMsg", usernameMsg);
            return "site/login";
        }
        if (map.containsKey("passwordMsg")){
            String passwordMsg = (String) map.get("passwordMsg");
            model.addAttribute("passwordMsg", passwordMsg);
            return "site/login";
        }

        // 没有错误，那就正常，接下来获取凭证
        LoginTicket ticket = (LoginTicket) map.get("ticket");
        // 设置 Cookie，并且将 cookie 响应给客户端
        Cookie cookie = new Cookie("ticket", ticket.getTicket());
        cookie.setPath(contextPath);
        cookie.setMaxAge(expiredSeconds);
        response.addCookie(cookie);

        // 重定向到首页
        return "redirect:/index";
    }


    /**
     * 退出登录，也就是使登录凭证失效
     * @param ticket 登录凭证
     */
    @LoginRequired  // 需要做登录检查
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        // 重定向到主页
        return "redirect:/index";
    }
}

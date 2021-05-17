package com.ronz.community.service;

import com.ronz.community.dao.LoginTicketMapper;
import com.ronz.community.dao.UserMapper;
import com.ronz.community.entity.LoginTicket;
import com.ronz.community.entity.User;
import com.ronz.community.util.CommonConstant;
import com.ronz.community.util.CommonUtils;
import com.ronz.community.util.HostHolder;
import com.ronz.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Session;
import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/4/25 20:42
 * @Version 1.0
 */

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private HostHolder hostHolder;


    @Value("${community.domain.path}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String project;

    /**
     * 查询单个用户 ID
     */
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    /**
     * 注册用户
     */
    public Map<String, String> registerUser(User user){
        Map<String, String> map = new HashMap<>();

        // 我觉得参数判空应该是前端的活~~
        if (user == null){
            throw new IllegalArgumentException("用户参数错误！");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        // 1. 首先判断用户是否已经注册
        User name = userMapper.selectByName(user.getUsername());
        if (name != null){
            map.put("usernameMsg", "账号已存在！");
            return map;
        }
        User email = userMapper.selectByEmail(user.getEmail());
        if (email != null){
            map.put("emailMsg", "邮箱已存在！");
            return map;
        }

        // 2. 设置用户的信息，然后插入到数据库中
        // 借用牛客网的头像 http://images.nowcoder.com/head/1t.png，为用户生成随机头像
        user.setHeaderUrl("http://images.nowcoder.com/head/" + new Random().nextInt(1001) + "t.png");
        // 给密码加点盐，然后 MD5 加密
        user.setSalt(CommonUtils.generateUUID().substring(0, 5));
        user.setPassword(CommonUtils.md5(user.getPassword()+user.getSalt()));
        user.setActivationCode(CommonUtils.generateUUID());
        user.setType(0);
        user.setStatus(0);
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 3. 发送激活邮件给用户
        // 构造 html
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        // 构造激活链接 http://localhost:8080/community/active/101/sldfjsfjlskdlf
        String activeUrl = domain + project + "/active/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("activeUrl", activeUrl);
        String s = templateEngine.process("mail/activation.html", context);
        mailClient.sendMail(user.getEmail(), "激活邮件", s);

        return map;
    }

    /**
     * 激活账号
     * @param id 用户ID
     * @param code 激活码
     */
    public CommonConstant active(int id, String code){
        User user = userMapper.selectById(id);
        if (user != null){
            int status = user.getStatus();
            // 首先判断账号是否已经激活过了
            if (status == CommonConstant.ACTIVATION_SUCCESS.getCode()){
                return CommonConstant.ACTIVATION_REPEAT;
            }

            // 否则尚未激活，执行激活过程
            // 首先判断验证码是否相等
            if (user.getActivationCode().equals(code)){
                // 验证码相等，则更新用户账号状态
                userMapper.updateStatus(id, CommonConstant.ACTIVATION_SUCCESS.getCode());
                return CommonConstant.ACTIVATION_SUCCESS;
            }
        }
        // 如果用户不存在或者验证码不相等，则账号激活失败
        return CommonConstant.ACTIVATION_FAILURE;
    }

    /**
     * 登录账号
     * @param username 账号
     * @param password 密码
     * @param expiredSeconds 过期时间
     * @return java.util.Map<java.lang.String,java.lang.String> 显示到前端的提示信息
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String ,Object> map = new HashMap<>();

        // 空值判断
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "请输入账号！");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "请输入密码！");
            return map;
        }

        // 账号验证
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "账号不存在！");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "账号未激活！");
            return map;
        }
        // 验证密码
        String md5 = CommonUtils.md5(password + user.getSalt());
        if (!md5.equals(user.getPassword())){
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        // 走到这里之后，说明账号密码正确且账户正常
        // 构造凭证信息
        LoginTicket loginTicket = new LoginTicket();
        String ticket = CommonUtils.generateUUID();
        loginTicket.setTicket(ticket);
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + (long)expiredSeconds * 1000));

        // 将凭证信息保存到数据库
        loginTicketMapper.insertLoginTicket(loginTicket);
        // 传递一份给客户端
        map.put("ticket", loginTicket);
        return map;
    }

    /**
     * 退出账号，也就是使登录凭证失效
     * @param ticket 登录凭证
     */
    public void logout(String ticket){
        // 使登录凭证失效
        loginTicketMapper.updateStatus(ticket, CommonConstant.LOGIN_TICKED_INVALID.getCode());
    }

    /**
     * 根据 ticket 查找用户凭证
     */
    public LoginTicket findLoginTicket(String ticket){
        if (!StringUtils.isBlank(ticket)){
            return loginTicketMapper.selectByTicket(ticket);
        }
        return null;
    }

    /**
     * 根据 ID 更细用户头像链接
     */
    public void updateHeaderUrl(int id, String headerUrl){
        userMapper.updateHeader(id, headerUrl);
    }

    /**
     * 更新密码
     * @param oldPassword 原先的密码
     * @param newPassword 新的密码
     * @return void
     */
    public Map<String, String> updatePassword(String oldPassword, String newPassword){
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(oldPassword)){
            map.put("opMsg", "请输入原始密码！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("npMsg", "请输入新密码！");
            return map;
        }
        // 查询旧密码，和填入的原始密码对比
        User user = hostHolder.get();
        String md5 = CommonUtils.md5(oldPassword + user.getSalt());
        if (!md5.equals(user.getPassword())){
            map.put("opMsg", "原始密码错误！");
            return map;
        }
        // 将新密码加密，然后更新到数据库中
        String newMd5 = CommonUtils.md5(newPassword + user.getSalt());
        userMapper.updatePassword(user.getId(), newMd5);
        return null;
    }
}

package com.ronz.community;

import com.ronz.community.util.CommonConstant;
import com.ronz.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/11 15:38
 * @Version 1.0
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {

    @Autowired
    private MailClient mailClient;

    // thymeleaf 模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendTextMail(){
        mailClient.sendMail("ronz_liu@163.com", "TEST", "Hello");
    }


    /**
     * 测试发送 HTML
     */
    @Test
    public void testSendHtmlMail(){
        // 设置模板页面的变量到容器中
        Context context = new Context();
        context.setVariable("username", "ronz");
        // 将 mail/demo.html 转换成字符串
        String s = templateEngine.process("mail/demo.html", context);
        // 发送
        mailClient.sendMail("ronz_liu@163.com", "HTML", s);
    }
}

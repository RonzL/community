package com.ronz.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @Description 使用 SpringBoot 的 JavaMailSender 组件发送邮件
 * @Author Ronz
 * @Date 2021/5/11 15:38
 * @Version 1.0
 */

@Component
public class MailClient {

    @Autowired
    private JavaMailSender mailSender;

    private final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Value("${spring.mail.username}")
    private String from;    // 发件人姓名


    /**
     * 发送邮件函数
     */
    public void sendMail(String to, String subject, String context){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            // 设置邮件信息
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(context, true);
            // 发送
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());
        }
    }

}

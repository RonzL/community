package com.ronz.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @Description 生成验证码的配置类，因为 Spring 并没有整合 kaptcha，所以需要自己编写配置类
 * @Author Ronz
 * @Date 2021/5/12 17:01
 * @Version 1.0
 */

@Configuration
public class VerificationProducerConfig {
    
    /**
     * 生成一个配置好的 kaptchaProducer 实例给 Spring 容器
     */
    @Bean
    public Producer kaptchaProducer(){
        // 配置验证码相关参数
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");   // 设置验证码图片宽度
        properties.setProperty("kaptcha.image.height", "40");   // 高度
        properties.setProperty("kaptcha.textproducer.font.size", "32"); // 设置字体大小
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0"); // 字体颜色
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789abcdefghijklmnopqrstuvwxyz"); //生成字母的范围
        properties.setProperty("kaptcha.textproducer.char.length", "4");    // 设置验证码的长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");    // 设置不给验证码图片添加干扰

        // 将配置信息添加到 kaptcha 中
        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);

        return kaptcha;
    }
}

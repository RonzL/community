package com.ronz.community;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.assertj.ApplicationContextAssertProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// 这个测试类可以认为在 main() 方法中调用 CommunityApplication.run()
// 这里为什么用这几个注解，在 《Spring 实战》 P15 有讲解
@SpringBootTest // 告诉 Junit 在启动测试的时候要添加上 Spring Boot 的功能
@RunWith(SpringRunner.class)    // @RunWith 用于指定一个运行器，这里就是指定 SpringRunner 来运行
@ContextConfiguration(classes = CommunityApplication.class) // 如果我们想要在测试类中使用容器中的 bean，就需要导入对应的配置文件（比如 xml），启动类可以看做是一个总的配置文件
class CommunityApplicationTests implements ApplicationContextAware {

    ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Test
    public void printContext(){
        System.out.println(context);
    }
}

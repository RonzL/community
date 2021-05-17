package com.ronz.community.util;

import com.ronz.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Description 持有用户信息，用来代替 session 对象
 * 为什么不直接设置到 session 之中呢？
 * 因为服务器是多线程的，如果直接设置到 session 之中，可能会出现线程安全问题
 * @Author Ronz
 * @Date 2021/5/13 21:48
 * @Version 1.0
 */

@Component
public class HostHolder {

    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    /**
     * 将用户设置到 ThreadLocal 之中
     */
    public void set(User user){
        threadLocal.set(user);
    }

    /**
     * 获取本线程持有的用户
     */
    public User get(){
        return threadLocal.get();
    }

    /**
     * 清除本线程持有的用户
     */
    public void clear(){
        threadLocal.remove();
    }

}

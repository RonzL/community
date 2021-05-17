package com.ronz.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解。
 *
 * 由于有一些方法需要登录之后才能访问，因此在访问这些方法之前，需要对用户的登录状态做检查
 * 可以通过拦截器实现对登录状态做检查，但是拦截器怎么知道有哪些方法需要做检查呢？
 * 可以通过自定义注解来标识。
 * 拦截器首先检查当前拦截的方法是否有自定义的注解，如果有，说明需要做登录检查。
 */


@Target(ElementType.METHOD)     // 注解作用于方法
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时起作用
public @interface LoginRequired {

}

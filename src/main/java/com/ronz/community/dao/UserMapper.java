package com.ronz.community.dao;

import com.ronz.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @Description 用户相关操作
 * @Author Ronz
 * @Date 2021/4/16 16:36
 * @Version 1.0
 */

// mybatis 的注解
// 虽然使用 mapper 注解，MapperTest 中注入会报红，但是不影响使用
@Mapper
@Component("userMapper")    // 加上只是为了自动注入的时候不报红，不加也行
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}

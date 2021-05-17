package com.ronz.community.dao;

import com.ronz.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/13 12:04
 * @Version 1.0
 */

@Mapper
@Component("loginTicketMapper") // 加上只是为了防止自动注入的时候报红，不加也行
public interface LoginTicketMapper {

    /**
     * 向数据库中插入一个登陆凭证
     */
    @Insert({
            "insert into login_ticket(user_id,ticket,status,expired)",
            "values(#{userId},#{ticket},#{status},#{expired})"
    })
    // 插入成功后，需要给 LoginTicket 对象设置生成的主键，通过 Options 注解来实现
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertLoginTicket(LoginTicket ticket);

    /**
     * 根据登陆凭证查找用户凭证
     */
    @Select({
            "select id,user_id,ticket,status,expired from login_ticket",
            "where ticket = #{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    /**
     * 根据登陆凭证更新用户状态
     * 一般情况下，让数据失效最好通过标志位实现，而不是删除数据
     */
    @Update({
            // 此处仅仅是为了演示注解形式下的动态 SQL 语句是如何写的，并无其他含义
            // 可以看到，相对于 xml 形式的动态 SQL，注解形式仅仅是多了 <script></script> 标签
            "<script>",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test=\"ticket != null\">",
            "and 1 = 1",
            "</if>",
            "</script>"
    })
    void updateStatus(String ticket, int status);
}

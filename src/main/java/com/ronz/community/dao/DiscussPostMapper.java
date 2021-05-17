package com.ronz.community.dao;

import com.ronz.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description 讨论帖相关操作
 * @Author Ronz
 * @Date 2021/4/20 22:10
 * @Version 1.0
 */
@Mapper
@Component("discussPostMapper") // 加上只是为了自动注入的时候不报红，不加也行
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 如果需要动态拼接 SQL，并且方法有且只有一个参数，必须要使用 @Param 注解
     * @param userId
     * @return int
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 增加文章到数据库中
     */
    void insertDiscussPost(DiscussPost discussPost);
}

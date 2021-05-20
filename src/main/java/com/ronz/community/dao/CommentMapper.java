package com.ronz.community.dao;

import com.ronz.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/18 10:43
 * @Version 1.0
 */

@Mapper
@Component("commentMapper")
public interface CommentMapper {

    /**
     * 分页查询 讨论帖/楼层 的评论
     * @param entityType 被查询目标类型(讨论帖评论/楼层评论)
     * @param entityId 被查询目标 ID
     * @param offset 页数
     * @param limit 每页记录数量
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);


    /**
     * 查询 讨论帖/楼层 的评论数量
     * @param entityType 被查询目标类型
     * @param entityId 被查询目标 ID
     */
    int selectCountByEntity(int entityType, int entityId);

    /**
     * 插入 讨论帖/楼层 的评论
     */
    int insertComment(Comment comment);
}

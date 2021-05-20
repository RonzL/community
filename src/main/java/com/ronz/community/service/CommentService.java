package com.ronz.community.service;

import com.ronz.community.dao.CommentMapper;
import com.ronz.community.entity.Comment;
import com.ronz.community.entity.DiscussPost;
import com.ronz.community.util.CommonConstant;
import com.ronz.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/18 11:07
 * @Version 1.0
 */

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 分页查询 主题帖/楼层 的评论
     */
    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 查询 主题帖/楼层 的评论条数
     */
    public int selectCountByEntity(int entityType, int entityId){
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 插入 主题帖/楼层 的评论
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public void insertComment(Comment comment){
        if (comment == null){
            return;
        }

        // 关键词过滤
        HtmlUtils.htmlEscape(comment.getContent());
        comment.setContent(sensitiveFilter.getFilteredStr(comment.getContent()));

        // 1. 首先插入评论
        int result = commentMapper.insertComment(comment);
        // 2. 判断是不是对讨论帖的评论
        if (comment.getEntityType() == CommonConstant.ENTITY_TYPE_POST.getCode()){
            // 如果是对讨论帖的回复，才更新
            // 首先查询出讨论帖已经有的评论数量
            DiscussPost post = discussPostService.selectDiscussPostById(comment.getEntityId());
            int commentCount = post.getCommentCount() + result;
            // 更新评论数量
            discussPostService.updateDiscussPostCommentCount(comment.getEntityId(), commentCount);
        }
    }
}

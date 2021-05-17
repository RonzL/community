package com.ronz.community.service;

import com.ronz.community.dao.DiscussPostMapper;
import com.ronz.community.entity.DiscussPost;
import com.ronz.community.entity.User;
import com.ronz.community.util.CommonUtils;
import com.ronz.community.util.HostHolder;
import com.ronz.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.util.Date;
import java.util.List;

/**
 * @Description 讨论帖 Service
 * @Author Ronz
 * @Date 2021/4/25 19:33
 * @Version 1.0
 */

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    // 查询讨论帖
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    // 查询讨论贴条数
    public int selectDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 添加讨论帖
     */
    public void insertDiscussPost(DiscussPost discussPost){
        // 标题和内容中可能包含 HTML 标签，所以要避免它们影响
        HtmlUtils.htmlEscape(discussPost.getTitle());
        HtmlUtils.htmlEscape(discussPost.getContent());
        // 敏感词过滤
        discussPost.setTitle(sensitiveFilter.getFilteredStr(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.getFilteredStr(discussPost.getContent()));

        discussPostMapper.insertDiscussPost(discussPost);
    }

}


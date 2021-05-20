package com.ronz.community.service;

import com.ronz.community.dao.MessageMapper;
import com.ronz.community.entity.Message;
import com.ronz.community.entity.Page;
import com.ronz.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/19 20:43
 * @Version 1.0
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 分页查询会话列表（每个会话都只包含一条最新的消息）
     */
    public List<Message> selectConversations(int userId, Page page){
        return messageMapper.selectConversations(userId, page.getOffset(), page.getLimit());
    }

    /**
     * 查询所有会话个数
     */
    public int selectConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询某个会话未读消息个数
     */
    public int selectConversationUnreadLetterCount(int userId, String conversationId) {
        return messageMapper.selectConversationUnreadLetterCount(userId, conversationId);
    }

    /**
     * 分页查询某个会话下的所有消息
     */
    public List<Message> selectConversationLetters(String conversationId, Page page){
        return messageMapper.selectConversationLetters(conversationId, page.getOffset(), page.getLimit());
    }

    /**
     * 查询某个会话下的消息数量
     */
    public int selectConversationLettersCount(String conversationId){
        return messageMapper.selectConversationLettersCount(conversationId);
    }

    /**
     * 插入一条会话消息
     */
    public int insertLetter(Message message){
        // 处理 HTML 标签以及过滤敏感词
        String content = sensitiveFilter.getFilteredStr(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(content);

        return messageMapper.insertLetter(message);
    }

    /**
     * 更新会话状态
     */
    public int updateStatus(List<Integer> ids, int status){
        if (!ids.isEmpty()){
            return messageMapper.updateStatus(ids, status);
        }
        return 0;
    }


}

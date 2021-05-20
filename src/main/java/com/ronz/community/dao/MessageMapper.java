package com.ronz.community.dao;

import com.ronz.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description 私信消息持久层
 * @Author Ronz
 * @Date 2021/5/19 20:00
 * @Version 1.0
 */

@Mapper
@Component("messageMapper")
public interface MessageMapper {

    /**
     * 分页查询某个用户的所有会话列表（每个会话只查询出最新的一条消息）
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询某个用户的所有会话个数
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个用户的未读消息条数
     *  - conversationID == null ---> 所有未读消息数量
     *  - conversationId != null ---> 该会话下的未读消息数量
     */
    int selectConversationUnreadLetterCount(int userId, String conversationId);

    /**
     * 分页查询某个会话的信息记录
     */
    List<Message> selectConversationLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话包含的消息数量
     */
    int selectConversationLettersCount(String conversationId);

    /**
     * 插入消息到数据库
     */
    int insertLetter(Message message);

    /**
     * 根据 id 更新一批消息的状态(因为同一个会话可能有好几个未读消息，但是点进去之后全部变成已读)：
     * status == 0 未读
     * status == 1 已读
     * status == 2 已删除
     */
    int updateStatus(List<Integer> ids, int status);
}

package com.ronz.community;

import com.ronz.community.dao.DiscussPostMapper;
import com.ronz.community.dao.LoginTicketMapper;
import com.ronz.community.dao.MessageMapper;
import com.ronz.community.dao.UserMapper;
import com.ronz.community.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/4/16 16:58
 * @Version 1.0
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testUserMapper(){
        User user = userMapper.selectById(101);
        System.out.println(user);

        User user1 = userMapper.selectByName("liubei");
        System.out.println(user1);
    }

    @Test
    public void testDiscussPost(){
        List<DiscussPost> discussPosts =
                discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : discussPosts){
            System.out.println(post);
        }


        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testLoginTicketMapper(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(1);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));  // 设置 10min 过期
        loginTicketMapper.insertLoginTicket(loginTicket);

        LoginTicket ticket = loginTicketMapper.selectByTicket("abc");
        System.out.println(ticket);

        loginTicketMapper.updateStatus("abc", 0);
    }

    @Test
    public void testMessageMapper(){
        int userId = 150;
        Page page = new Page();
        page.setCurPage(1);
        page.setLimit(5);
        page.setRows(20);

        List<Message> messages = messageMapper.selectConversations(userId, page.getOffset(), page.getLimit());
        for (Message message : messages){
            System.out.println(message);
        }

    }


}

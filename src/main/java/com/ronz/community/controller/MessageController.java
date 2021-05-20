package com.ronz.community.controller;

import com.ronz.community.annotation.LoginRequired;
import com.ronz.community.entity.Message;
import com.ronz.community.entity.Page;
import com.ronz.community.entity.User;
import com.ronz.community.service.MessageService;
import com.ronz.community.service.UserService;
import com.ronz.community.util.CommonConstant;
import com.ronz.community.util.CommonUtils;
import com.ronz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/19 21:25
 * @Version 1.0
 */

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    /**
     * 分页查询会话列表
     */
    @LoginRequired
    @GetMapping("/conversations")
    public String findAllConversations(Model model, Page page){
        User user = hostHolder.get();

        page.setLimit(5);
        page.setRows(messageService.selectConversationCount(user.getId()));
        page.setPath("/message/conversations");

        List<Message> messageList = messageService.selectConversations(user.getId(), page);

        List<Map<String, Object>> list = new ArrayList<>();
        if (messageList != null){
            for (Message message : messageList){
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("unread", messageService.selectConversationUnreadLetterCount(user.getId(), message.getConversationId()));
                int peerId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                User peer = userService.findUserById(peerId);
                map.put("peer", peer);
                list.add(map);
            }
        }
        int allUnread = messageService.selectConversationUnreadLetterCount(user.getId(), null);
        model.addAttribute("allUnread", allUnread);
        model.addAttribute("messageList", list);
        return "site/letter";
    }

    /**
     * 分页查询某个会话下的消息
     */
    @LoginRequired
    @GetMapping("/letters/{conversationId}")
    public String findConversationLetter(Model model,@PathVariable("conversationId") String conversationId, Page page){

        // 设置分页信息
        page.setLimit(5);
        page.setRows(messageService.selectConversationLettersCount(conversationId));
        page.setPath("/message/letters/" + conversationId);

        List<Message> messageList = messageService.selectConversationLetters(conversationId, page);

        List<Map<String, Object>> list = new ArrayList<>();
        if (messageList != null){
            for (Message message : messageList){
                Map<String, Object> map = new HashMap<>();
                map.put("message", message);
                map.put("from", userService.findUserById(message.getFromId()));
                list.add(map);
            }
        }
        model.addAttribute("messageList", list);
        model.addAttribute("peer", getPeer(conversationId));

        // 设置此会话下的未读消息变为已读
        List<Integer> ids = getIds(messageList);
        messageService.updateStatus(ids, CommonConstant.MESSAGE_READED.getCode());

        return "site/letter-detail";
    }

    /**
     * 增加一条会话消息(发送消息给某人)
     */
    @ResponseBody
    @PostMapping("/add/{toId}")
    public String addLetter(@PathVariable("toId") int toId, Message message){
        if (message == null || StringUtils.isBlank(message.getContent())){
            return CommonUtils.getJSONString(400, "消息不能为空！");
        }

        User user = hostHolder.get();
        message.setFromId(user.getId());
        message.setStatus(CommonConstant.MESSAGE_UNREAD.getCode());
        message.setCreateTime(new Date());
        message.setToId(toId);
        int prefix =toId < user.getId() ? toId : user.getId();
        int suffix = toId < user.getId() ? user.getId() : toId;
        message.setConversationId(prefix + "_" + suffix);
        messageService.insertLetter(message);
        return CommonUtils.getJSONString(0, "发送成功！");
    }


    /**
     * 获取通信对端用户
     */
    public User getPeer(String conversationId){
        User user = hostHolder.get();
        String[] split = conversationId.split("_");
        Integer peerId = user.getId() != Integer.parseInt(split[0]) ? Integer.parseInt(split[0]) : Integer.parseInt(split[1]);
        return userService.findUserById(peerId);
    }

    /**
     * 找到某个会话下的所有未读消息的 ID
     */
    public List<Integer> getIds(List<Message> messageList){
        User user = hostHolder.get();
        List<Integer> list = new ArrayList<>();
        for (Message message : messageList){
            if (message.getStatus() == 0 && message.getToId() == user.getId()){
                list.add(message.getId());
            }
        }
        return list;
    }
}

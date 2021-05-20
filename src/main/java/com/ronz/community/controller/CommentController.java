package com.ronz.community.controller;

import com.ronz.community.annotation.LoginRequired;
import com.ronz.community.entity.Comment;
import com.ronz.community.service.CommentService;
import com.ronz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/19 11:28
 * @Version 1.0
 */

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private HostHolder hostHolder;


    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        if (comment == null || StringUtils.isBlank(comment.getContent())){
            // TODO 提交内容为空时，需要有个提示框提示
            return "redirect:/discuss/detail/" + discussPostId;
        }

        comment.setCreateTime(new Date());
        comment.setUserId(hostHolder.get().getId());
        comment.setStatus(0);

        commentService.insertComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;

    }

}

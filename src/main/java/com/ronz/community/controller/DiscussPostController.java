package com.ronz.community.controller;

import com.ronz.community.dao.DiscussPostMapper;
import com.ronz.community.entity.DiscussPost;
import com.ronz.community.entity.User;
import com.ronz.community.service.DiscussPostService;
import com.ronz.community.util.CommonUtils;
import com.ronz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @Description 讨论帖控制器
 * @Author Ronz
 * @Date 2021/5/17 20:59
 * @Version 1.0
 */

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 发布文章
     */
    @PostMapping("/add")
    @ResponseBody   // 使用此注解，会返回 JSON 数据
    public String addDiscussPost(String title, String content){
        // 首先判断用户是否登录
        User user = hostHolder.get();
        if (user == null){
            return CommonUtils.getJSONString(403, "您还没有登录，请先登录！");
        }
        // 如果用户登录了，判断标题和内容是否为空
        if (StringUtils.isBlank(title) || StringUtils.isBlank(content)){
            return CommonUtils.getJSONString(400, "标题/内容 不能为空！");
        }

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.insertDiscussPost(discussPost);

        // 错误信息，将来有统一处理器来处理
        return CommonUtils.getJSONString(0, "发布成功！");
    }

}

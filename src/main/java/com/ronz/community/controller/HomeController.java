package com.ronz.community.controller;

import com.ronz.community.entity.DiscussPost;
import com.ronz.community.entity.Page;
import com.ronz.community.entity.User;
import com.ronz.community.service.DiscussPostService;
import com.ronz.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/4/25 20:52
 * @Version 1.0
 */

@Controller
public class HomeController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    /**
     * 请求首页
     */
    @GetMapping(path = "/index")
    public String getIndexPage(Model model, Page page){
        page.setRows(discussPostService.selectDiscussPostRows(0));  // 查询所有记录条数并设置到分页实例中
        page.setPath("/index"); // 设置分页查询路径

        // 查询首页帖子
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit());

        // 2.将每个讨论帖的用户信息以及讨论帖内容存放到一个 map 集合中
        // 然后把 map 集合存放到一个 list 集合中，因为首页肯定有很多用户以及讨论帖
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post : list){
                // 将每个讨论帖存放到一个 map 集合中
                Map<String, Object> map = new HashMap<>();

                // 保存讨论帖信息
                map.put("post", post);

                // 保存用户信息
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                // 加入到 list 集合中
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "index";
    }
}

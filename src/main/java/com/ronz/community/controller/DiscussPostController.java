package com.ronz.community.controller;

import com.ronz.community.dao.DiscussPostMapper;
import com.ronz.community.dao.UserMapper;
import com.ronz.community.entity.Comment;
import com.ronz.community.entity.DiscussPost;
import com.ronz.community.entity.Page;
import com.ronz.community.entity.User;
import com.ronz.community.service.CommentService;
import com.ronz.community.service.DiscussPostService;
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
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;

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

    /**
     * 根据讨论帖 ID 查询讨论帖
     */
    @GetMapping("/detail/{discussPostId}")
    public String findDiscussPostById(Model model, @PathVariable("discussPostId") int discussPostId, Page page){
        // 保存帖子内容和作者
        DiscussPost post = discussPostService.selectDiscussPostById(discussPostId);
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("post", post);

        // 主题帖评论的分页信息
        page.setLimit(10);   // 每页显示 10 条回帖（不包括楼中楼）
        page.setPath("/discuss/detail/" + discussPostId);   // 设置分页查询路径
        page.setRows(post.getCommentCount());

        // 先约定如下
        // 评论: 给帖子的评论
        // 回复: 给评论的评论

        // 首先获取分页后的评论列表
        List<Comment> comments = commentService.selectCommentsByEntity(
                CommonConstant.ENTITY_TYPE_POST.getCode(),
                post.getId(),
                page.getOffset(),
                page.getLimit());

        // 一个 map 存放着一条评论的所有信息(评论内容、评论人、楼中楼)
        List<Map<String, Object>> commentVoList = new ArrayList<>();

        // 下面要遍历评论列表，依次添加评论列表
        if (comments != null){
            for (Comment comment : comments){
                // 一个 map 存放着一条评论的所有信息(评论内容、评论人、楼中楼)
                HashMap<String, Object> commentVo = new HashMap<>();

                // 首先添加评论内容以及评论人
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 下面开始准备添加楼中楼
                // 获取当前楼层评论的楼中楼回复列表
                List<Comment> replyList = commentService.selectCommentsByEntity(
                        CommonConstant.ENTITY_TYPE_REPLY.getCode(),
                        comment.getId(),
                        0,
                        Integer.MAX_VALUE
                );

                // 回复 VO 列表，存放当前楼层的所有楼中楼
                ArrayList<Map<String, Object>> replyVoList = new ArrayList<>();

                // 遍历当前楼层的楼中楼回复
                if (replyList != null){
                    for (Comment reply : replyList){
                        // 构造一条楼中楼回复(回复正文、回复人、回复给谁)
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        // 提那家到当前楼层的回复列表中
                        replyVoList.add(replyVo);
                    }
                }
                // 添加到当前楼层的 map 中
                commentVo.put("replies", replyVoList);

                int replyCount = commentService.selectCountByEntity(
                        CommonConstant.ENTITY_TYPE_REPLY.getCode(),
                        comment.getId()
                );
                // 添加到当前楼层的 map 中
                commentVo.put("replyCount", replyCount);

                // 添加到当前讨论帖的所有评论集合中
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "site/discuss-detail";
    }

}

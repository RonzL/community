package com.ronz.community.controller;

import com.ronz.community.annotation.LoginRequired;
import com.ronz.community.entity.User;
import com.ronz.community.service.UserService;
import com.ronz.community.util.CommonUtils;
import com.ronz.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/14 13:43
 * @Version 1.0
 */

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    String contextPath;
    @Value("${community.header.path}")
    String uploadPath;
    @Value("${community.domain.path}")
    String domain;

    /**
     * 获取账户设置页面
     */
    @LoginRequired  // 需要做登录检查
    @GetMapping("/setting")
    public String getSettingPage(){
        return "site/setting";
    }

    /**
     * 上传头像
     * 1. 用户上传头像，然后判空
     * 2. 将用户上传的头像修改名称，然后存储到本地
     * 3. 生成获取头像的链接，然后存储到数据库中
     * 4. 跳转到获取头像的路径
     * @param headerImage 头像文件
     * @param model 返回值前端信息用
     */
    @LoginRequired  // 需要做登录检查
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error", "请先选择图片再上传！");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        // 获取文件后缀，如 123.png --> .png
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 随机生成文件名，将来要存储在服务器
        filename = CommonUtils.generateUUID() + suffix;

        // 将用户上传的头像以新的文件名存储到本地服务器
        try {
            File file = new File(uploadPath + "/" + filename);
            headerImage.transferTo(file);
        } catch (IOException e) {
            logger.error("服务器出错，文件上传失败：" + e.getMessage());
            model.addAttribute("error", "文件上传失败，请稍后再试！");
            return "/site/setting";
        }

        // 构造头像链接，然后把头像链接更新到数据库中
        // localhost:8080/community/header/xxxxx.png
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        // 获取当前正在登陆的用户
        User user = hostHolder.get();
        // 将头像链接更新到数据库
        userService.updateHeaderUrl(user.getId(), headerUrl);
        // 跳转到主页
        return "redirect:/index";
    }


    /**
     * 异步请求获取头像
     * @param filename 头像文件名
     * @param response 将来要响应头像给前端
     */
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        // 获取服务器存放文件路径
        String path = uploadPath + "/" + filename;
        // 获取文件后缀，用于设置 response 返回格式
        String suffix = path.substring(path.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            logger.error("头像格式不正确：");
            return;
        }

        // 设置 response 返回格式
        response.setContentType("image/" + suffix);

        try {
            // 将头像通过 response 写回去
            InputStream inputStream = new FileInputStream(path);
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;

            // 写到 response 的 body 中
            while ((len = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("加载头像失败：" + e.getMessage());
        }
    }

    @LoginRequired  // 需要做登录检查
    @PostMapping("/update")
    public String updatePassword(Model model, String oldPassword, String newPassword){
        Map<String, String> map = userService.updatePassword(oldPassword, newPassword);
        if (map != null){
            if (map.containsKey("opMsg")){
                model.addAttribute("opMsg", map.get("opMsg"));
                return "site/setting";
            }
            if (map.containsKey("npMsg")){
                model.addAttribute("npMsg", map.get("npMsg"));
                return "site/setting";
            }
        }

        // 走到这里，说明更新成功了
        // 跳转到首页
        return "redirect:/index";
    }

}

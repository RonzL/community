package com.ronz.community.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @Description 生成随机字符串、对密码进行 MD5 加密
 * @Author Ronz
 * @Date 2021/5/11 20:48
 * @Version 1.0
 */
public class CommonUtils {

    /**
     * 生成随机字符串
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对密码进行 MD5 加密之后返回
     */
    public static String md5(String password){
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 将字符串转换为 JSON 格式的对象
     * @param code 返回给客户端的状态码
     * @param msg 返回给客户端的消息提示
     * @param map 返回给客户端的消息提示细节
     */
    public static String getJSONString(int code, String msg, Map<String, String> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        for (String item : map.keySet()){
            jsonObject.put(item, map.get(item));
        }
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code, Map<String, String> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        for (String item : map.keySet()){
            jsonObject.put(item, map.get(item));
        }
        return jsonObject.toJSONString();
    }
}

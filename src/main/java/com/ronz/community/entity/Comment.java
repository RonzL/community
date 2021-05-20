package com.ronz.community.entity;

import java.util.Date;

/**
 * @Description 讨论帖/楼层 的评论实体类
 * @Author Ronz
 * @Date 2021/5/18 10:43
 * @Version 1.0
 */
public class Comment {

    private int id;
    private int userId; // 发出评论的用户 id
    private int entityType; // 被评论的目标类别，如：1-主题帖评论、2-楼层评论
    private int entityId;   // 被评论的帖子(主题帖/楼层)的 id
    private int targetId;   // 被评论的用户的 id
    private String content;
    private int status;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

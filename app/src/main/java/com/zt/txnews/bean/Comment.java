package com.zt.txnews.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/11.
 * 评论表
 */
public class Comment extends BmobObject {
    private String commentId;
    private String userId;//外键
    private String invitationId;//外键

    private String name;  //评论人名字
    private String commentContent; //评论内容
    private String iconUrl;  //评论人头像地址
    private int dianzangCount;//赞

    private String towho;//对谁
    private String toTitle;//对哪条帖子title
    private int isPressZhan;//0表示没有点赞 1表示该条评论被点赞了，据次来显示点赞图标的状态，并不在Bmob段更新，只是在listview的适配器里改变

    public int getIsPressZhan() {
        return isPressZhan;
    }

    public void setIsPressZhan(int isPressZhan) {
        this.isPressZhan = isPressZhan;
    }

    public int getDianzangCount() {
        return dianzangCount;
    }

    public void setDianzangCount(int dianzangCount) {
        this.dianzangCount = dianzangCount;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTowho() {
        return towho;
    }

    public void setTowho(String towho) {
        this.towho = towho;
    }

    public String getToTitle() {
        return toTitle;
    }

    public void setToTitle(String toTitle) {
        this.toTitle = toTitle;
    }
}

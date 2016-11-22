package com.zt.txnews.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/11.
 * 回复表
 */
public class Reply extends BmobObject {
    private String replyId;//回复表主键ID
    private String userId;  //外键
    private String invitationId; //外键
    private String commentId;//外键

    private String name;
    private String replyContent;
    private String iconUrl;

    private String toWho; //对谁
    private String toTitle; //对哪条帖子
    private String toComment, toReply; //对那条评论，回复

    private int type;// type=1 对评论的回复 type=2 对回复的回复


    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
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

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getToWho() {
        return toWho;
    }

    public void setToWho(String toWho) {
        this.toWho = toWho;
    }

    public String getToTitle() {
        return toTitle;
    }

    public void setToTitle(String toTitle) {
        this.toTitle = toTitle;
    }

    public String getToComment() {
        return toComment;
    }

    public void setToComment(String toComment) {
        this.toComment = toComment;
    }

    public String getToReply() {
        return toReply;
    }

    public void setToReply(String toReply) {
        this.toReply = toReply;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

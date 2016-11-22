package com.zt.txnews.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/9/14.
 * 帖子表 （评论和回复功能的4表定则）
 */
public class Invitation extends BmobObject{
    private String invitationId;
    private String userId;//外键id(用户表的主键objectId)

    private String name;  //发帖人名字
    private String iconUrl;//头像地址
    private String title;//帖子标题
    private BmobFile image;//图片
    private String content; //帖子内容
    private int dianzangCount;//赞
    private String type; //帖子类型

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(String invitationId) {
        this.invitationId = invitationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDianzangCount() {
        return dianzangCount;
    }

    public void setDianzangCount(int dianzangCount) {
        this.dianzangCount = dianzangCount;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Invitation{" +
                "invitationId='" + invitationId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", title='" + title + '\'' +
                ", image=" + image +
                ", content='" + content + '\'' +
                ", dianzangCount=" + dianzangCount +
                ", type='" + type + '\'' +
                '}';
    }
}

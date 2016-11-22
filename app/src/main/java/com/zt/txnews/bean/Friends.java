package com.zt.txnews.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/19.
 * 添加关注的好友表
 */
public class Friends extends BmobObject{
    private String meId;
    private String meName;
    private String friendsId;
    private String friendsName;
    private String friendPhotoIcon;//好友头像地址
    private boolean isGuanzhu;//是否关注

    public String getFriendPhotoIcon() {
        return friendPhotoIcon;
    }

    public void setFriendPhotoIcon(String friendPhotoIcon) {
        this.friendPhotoIcon = friendPhotoIcon;
    }

    public boolean isGuanzhu() {
        return isGuanzhu;
    }

    public void setIsGuanzhu(boolean isGuanzhu) {
        this.isGuanzhu = isGuanzhu;
    }

    public String getMeId() {
        return meId;
    }

    public void setMeId(String meId) {
        this.meId = meId;
    }

    public String getMeName() {
        return meName;
    }

    public void setMeName(String meName) {
        this.meName = meName;
    }

    public String getFriendsId() {
        return friendsId;
    }

    public void setFriendsId(String friendsId) {
        this.friendsId = friendsId;
    }

    public String getFriendsName() {
        return friendsName;
    }

    public void setFriendsName(String friendsName) {
        this.friendsName = friendsName;
    }
}

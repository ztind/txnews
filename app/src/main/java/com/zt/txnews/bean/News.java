package com.zt.txnews.bean;

/**
 * Created by Administrator on 2016/9/9.
 * 新闻对象类
 */
public class News {
    public String title;
    public String updateTime;
    public String authorName;
    public String picUrl;
    public String url;

    public News() {
    }

    public News(String title, String updateTime, String authorName, String picUrl, String url) {

        this.title = title;
        this.updateTime = updateTime;
        this.authorName = authorName;
        this.picUrl = picUrl;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", authorName='" + authorName + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

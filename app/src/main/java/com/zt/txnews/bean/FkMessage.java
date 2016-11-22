package com.zt.txnews.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/21.
 * 用户反馈
 */
public class FkMessage extends BmobObject {
    private String name;
    private String message;//反馈信息
    private String number;//联系方式

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

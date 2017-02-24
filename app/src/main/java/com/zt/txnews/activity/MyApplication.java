package com.zt.txnews.activity;

import android.app.Application;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.sharesdk.framework.ShareSDK;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/9/9.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //设置BmobConfig
        BmobConfig config =new BmobConfig.Builder()
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(60)
                        //文件分片上传时每片的大小（单位字节），默认512*1024
                .setBlockSize(500*1024)
                .build();
        Bmob.getInstance().initConfig(config);

        //init SMSSDK
        SMSSDK.initSDK(getApplicationContext(), "1707e6597cb56", "badc984ae4f73b633bee474c30326679");
        //init bmob
        Bmob.initialize(getApplicationContext(),"a9794c3f638004f0443358c1f2ca0fbe");

        ShareSDK.initSDK(this);

    }
}

package com.zt.txnews.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/9/11.
 * Toast的单列设计模式
 */
public class ShowToas {
    private static Toast instance;
    private ShowToas(){}
    public static void showToast(Context context,String message) {
        if (instance==null) {
            instance = Toast.makeText(context, message, Toast.LENGTH_SHORT);

        }else{
            instance.setText(message);
        }
        instance.show();
    }
}

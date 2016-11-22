package com.zt.txnews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.zt.txnews.R;

/**
 * Created by Administrator on 2016/9/22.
 */
public class SplahsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.sendEmptyMessageDelayed(1111, 2000);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1111){
                finish();
                startActivity(new Intent(SplahsActivity.this,MainActivity.class));
            }
        }
    };
}

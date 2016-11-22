package com.zt.txnews.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zt.txnews.R;
import com.zt.txnews.bean.User;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/9/20.
 */
public class SystemSettingActivity extends Activity implements View.OnClickListener{
    private RelativeLayout messageEdit_relative,clearAllChear_relative,idearfack_relative;
    private TextView exit_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        messageEdit_relative = (RelativeLayout) findViewById(R.id.r2);
        clearAllChear_relative = (RelativeLayout) findViewById(R.id.r3);
        idearfack_relative = (RelativeLayout) findViewById(R.id.r4);
        exit_text = (TextView) findViewById(R.id.outLogin_text);
        messageEdit_relative.setOnClickListener(this);
        clearAllChear_relative.setOnClickListener(this);
        idearfack_relative.setOnClickListener(this);
        exit_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.r2:
                startActivity(new Intent(this,MyMessageEditActivity.class));
                break;
            case R.id.r3:
                showClearAllCacheResultDialog();
                break;
            case R.id.r4:
                startActivity(new Intent(this,IdearReplyActivity.class));
                break;
            case R.id.outLogin_text:
                User user = BmobUser.getCurrentUser(this, User.class);
                if (user!=null){
                    BmobUser.logOut(this);
                    finish();
                }
                break;
        }
    }
    private void showClearAllCacheResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要清除所有缓存吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BmobQuery.clearAllCachedResults(SystemSettingActivity.this);
            }
        }).setNegativeButton("取消", null).show();
    }
    public void onCloseClick(View view){
        finish();
    }
}

package com.zt.txnews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zt.txnews.R;
import com.zt.txnews.bean.FkMessage;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/9/20.
 * 意见反馈界面
 */
public class IdearReplyActivity extends Activity implements View.OnClickListener{
    private EditText messageEdit, numberEdit;
    private TextView sendMessageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ideareply);
        initView();
    }

    private void initView() {
        messageEdit = (EditText) findViewById(R.id.idea_mesasge_edit);
        numberEdit = (EditText) findViewById(R.id.idea_number);
        sendMessageText = (TextView) findViewById(R.id.idea_sendtext);
        sendMessageText.setOnClickListener(this);
    }

    public void onCloseClick(View view) {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.idea_sendtext) {
            String message = messageEdit.getText().toString().trim();
            String number = numberEdit.getText().toString().trim();
            if (TextUtils.isEmpty(message)){
                ShowToas.showToast(this,"请填写你遇到的问题");
                return;
            }
            if (TextUtils.isEmpty(number)){
                ShowToas.showToast(this,"选填不能为空");
                return;
            }
            User currentUser = BmobUser.getCurrentUser(this, User.class);
            FkMessage fkMessage = new FkMessage();
            if (currentUser==null){
                fkMessage.setName("匿名");
            }else{
                fkMessage.setName(currentUser.getNickname());
            }
            fkMessage.setMessage(message);
            fkMessage.setNumber(number);
            fkMessage.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    ShowToas.showToast(IdearReplyActivity.this,"信息反馈成功");
                    finish();
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });

        }
    }
}

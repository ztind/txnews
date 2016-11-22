package com.zt.txnews.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zt.txnews.R;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/9/11.
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private ImageView backImage;
    private TextView registerText;
    private EditText userName,userPass;
    private Button loginBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        backImage = (ImageView) findViewById(R.id.login_back_image);
        registerText= (TextView) findViewById(R.id.register_text);
        userName = (EditText) findViewById(R.id.login_username_edit);
        userPass = (EditText) findViewById(R.id.login_pass_edit);
        loginBut = (Button) findViewById(R.id.login_button);
        //click listeren
        backImage.setOnClickListener(this);
        registerText.setOnClickListener(this);

        userName.addTextChangedListener(new MyWatcher());
        userPass.addTextChangedListener(new MyWatcher());
        loginBut.setOnClickListener(this);
        loginBut.setClickable(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_back_image:
                finish();
                break;
            case R.id.register_text:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.login_button:
                login();
                break;
        }
    }
    private void login() {
        String username = userName.getText().toString().toString();
        String pass = userPass.getText().toString().trim();
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(pass)){
            ShowToas.showToast(this,"输入信息为空");
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(pass);
        user.login(this, new SaveListener() {
            @Override
            public void onSuccess() {
//                Log.v("TAG", "登陆成功,登陆成功后or修改成功 User的所有字段会本地自动缓存");
                ShowToas.showToast(LoginActivity.this, "登陆成功");
                finish();
            }
            @Override
            public void onFailure(int i, String s) {
//                Log.v("TAG", "登陆失败" + i + "  " + s);
                if (i == 101) {
                    ShowToas.showToast(LoginActivity.this, "用户名或密码错误");
                }
                if (i == 9016) {
                    ShowToas.showToast(LoginActivity.this, "网络链接异常，请检查你的网络链接");
                }
            }
        });
    }

    class  MyWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                loginBut.setPressed(true);
                loginBut.setClickable(true);
            } else {
                loginBut.setPressed(false);
                loginBut.setClickable(false);
            }
        }
    }
}

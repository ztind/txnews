package com.zt.txnews.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zt.txnews.R;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/9/11.
 * 注册界面
 */
public class RegisterActivity extends Activity implements View.OnClickListener {
    private ImageView backImage;
    private EditText nickNmae,phoneEdit,passEdit,repassEdit,authcodeEdit;
    private Button getCodeBut,registerBut;
    private MyCountDowmTimer myCountDowmTimer;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        //init sms
        initReceiverCode();
        myCountDowmTimer = new MyCountDowmTimer(60000, 1000);
        //加载头像图片进入sd卡
        picIntoSd();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("注册中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("注册");
        progressDialog.setCancelable(false);
    }

    private void initView() {
        backImage = (ImageView) findViewById(R.id.register_back_image);
        nickNmae = (EditText) findViewById(R.id.register_nickname_edit);
        phoneEdit = (EditText) findViewById(R.id.register_phone_edit);
        passEdit = (EditText) findViewById(R.id.register_pass_edit);
        repassEdit = (EditText) findViewById(R.id.register_repass_edit);
        authcodeEdit = (EditText) findViewById(R.id.register_authcode_edit);
        getCodeBut = (Button) findViewById(R.id.register_getauthcode_button);
        registerBut = (Button) findViewById(R.id.register_button);
        //set click listeren
        backImage.setOnClickListener(this);
        getCodeBut.setOnClickListener(this);
        registerBut.setOnClickListener(this);

        //添加文本监听接口
        nickNmae.addTextChangedListener(new MyEditWatcher());
        phoneEdit.addTextChangedListener(new MyEditWatcher());
        passEdit.addTextChangedListener(new MyEditWatcher());
        repassEdit.addTextChangedListener(new MyEditWatcher());
        authcodeEdit.addTextChangedListener(new MyEditWatcher());

        phoneEdit.addTextChangedListener(new MyEditWatcher2());

        registerBut.setClickable(false);
        getCodeBut.setClickable(false);
    }
    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.register_back_image://back
             finish();
             break;
         case R.id.register_getauthcode_button://获取验证码
             getauthCode();
             break;
         case R.id.register_button://注册按钮
             register();
             break;
      }
    }
    private void getauthCode() {
        //获取验证码操作
        smsFlage = 1;

        String phone = phoneEdit.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || phone.length()!=11) {
            ShowToas.showToast(this, "请输入有效的手机号");
        } else {
            //获取验证码,调用此方法后执行其回掉方法将结果返回
            getSmsCode("86", phone);
            myCountDowmTimer.start();
        }

    }

    private String nickname;
    private String username;
    private String pass;

    private void register() {
        //验证验证码操作
          smsFlage = 2;

          nickname = nickNmae.getText().toString().trim();
          username = phoneEdit.getText().toString().trim();
          pass = passEdit.getText().toString().trim();
         String repass = repassEdit.getText().toString().trim();
         String code = authcodeEdit.getText().toString().trim();

        if(TextUtils.isEmpty(nickname) || TextUtils.isEmpty(username) ||TextUtils.isEmpty(pass) ||TextUtils.isEmpty(repass) ||TextUtils.isEmpty(code)) {
            ShowToas.showToast(this, "输入信息不能为空");
            return;
        }
        if (username.length()!=11) {
            ShowToas.showToast(this, "请输入有效的手机号");
            return;
        }
        if (!pass.equals(repass)){
            ShowToas.showToast(this, "两次密码不一致");
            return;
        }
        //提交验证码，调用此方法后执行其回掉方法将结果返回
        submitCode("86", username, code);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHandler();
    }
    //获取验证码的倒计时器类
    class  MyCountDowmTimer extends CountDownTimer{

        public MyCountDowmTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getCodeBut.setText(millisUntilFinished / 1000 + " s");
            getCodeBut.setClickable(false);
        }

        @Override
        public void onFinish() {
            getCodeBut.setText("获取验证码");
            getCodeBut.setClickable(true);
            getCodeBut.setPressed(true);
        }
    }
    //EditText的文本观察家
    class MyEditWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())){
                registerBut.setPressed(false);
                registerBut.setClickable(false);
            }else {
                registerBut.setPressed(true);
                registerBut.setClickable(true);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    class MyEditWatcher2 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s.toString())){
                getCodeBut.setPressed(false);
                getCodeBut.setClickable(false);
            }else {
                getCodeBut.setPressed(true);
                getCodeBut.setClickable(true);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    //将图片加载进入sd卡最后在取出来作为头像
    private File file;
    private String pic_sd_path;
    private void picIntoSd() {

        //版本大于6.0的情况 6.0系统在权限和sd卡读写方面做了改变
        if(Build.VERSION.SDK_INT>=23) {
            return;
        }

        InputStream is = null;
        try {
            is = getResources().getAssets().open("defaulticon.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        pic_sd_path = Environment.getExternalStorageDirectory().getPath() + "/defaulticon.jpg";
        file = new File(pic_sd_path);
        FileOutputStream out=null;

        if (!file.exists()) {
            int len;
            try {
                byte[] bytes = new byte[1024];
                out = new FileOutputStream(file);
                while ((len=is.read(bytes))!=-1){
                        out.write(bytes,0,len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (out!=null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is!=null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
//            Log.v("TAG", "图片已经存在sd卡 " +file.length()+ file.getAbsolutePath());
        }
    }
    //注册bmob
    public  void registerUser() {
        //6.0系统
        if(Build.VERSION.SDK_INT>=23) {
            User user = new User();
            user.setNickname(nickname);
            user.setUsername(username);
            user.setPassword(pass);
            user.setSex("男");
            user.setBrithday("2001-1-1");
            user.setIcon(null);
            user.setMotto("***我的个性签名***");

            user.signUp(RegisterActivity.this, new SaveListener() {
                @Override
                public void onSuccess() {
//                    Log.v("TAG", "注册成功");
                    progressDialog.cancel();
                    ShowToas.showToast(RegisterActivity.this, "注册成功");
                    finish();
                }
                @Override
                public void onFailure(int i, String s) {
//                    Log.v("TAG", "注册失败" + i + "---" + s.toString());
                    if(i==202){
                        ShowToas.showToast(RegisterActivity.this,"此号码已经注册过了，换个试试");
                    }
                    progressDialog.cancel();
                }
            });
            return;
        }
        //6.0以下系统
        progressDialog.show();
        File iconfile = new File(pic_sd_path);
        final BmobFile bmobFile = new BmobFile(file);//头像文件

        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                User user = new User();
                user.setNickname(nickname);
                user.setUsername(username);
                user.setPassword(pass);
                user.setIcon(bmobFile);
                user.setSex("男");
                user.setBrithday("2001-1-1");
                user.setMotto("***我的个性签名***");

                user.signUp(RegisterActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.cancel();
                        ShowToas.showToast(RegisterActivity.this, "注册成功");
                        finish();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        if(i==202){
                            ShowToas.showToast(RegisterActivity.this,"此号码已经注册过了，换个试试");
                        }
                        progressDialog.cancel();
                    }
                });
            }
            @Override
            public void onFailure(int i, String s) {
                if (i==9015){
                    ShowToas.showToast(RegisterActivity.this,"网络链接异常，请检查你的网络链接");
                }
                progressDialog.cancel();
            }
        });

    }
    /**
     * 短信验证码操作,以后用到直接copy下面代码即可
     */
    //1 注册回调接口 初始化短信接送监听器【注意：回掉接口在非UI线程中so要用到Handler来通知用户】
    private EventHandler eh;
    public int smsFlage = 0;//0:设置为初始化值 1：请求获取验证码 2：提交用户输入的验证码判断是否正确

    private void initReceiverCode(){
        this.eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        myHandler.sendEmptyMessage(1);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        myHandler.sendEmptyMessage(2);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    //此语句代表接口返回失败
                    //获取验证码失败。短信验证码验证失败（用flage标记来判断）
                    if (smsFlage==1) {
                        myHandler.sendEmptyMessage(3);
                    }else if (smsFlage==2){
                        myHandler.sendEmptyMessage(4);
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh);//注册短信回调
    }

    //2 获取短信验证码 请求获取短信验证码，在监听中返回
    private void getSmsCode(String country, String phone){
        SMSSDK.getVerificationCode(country, phone);//请求获取短信验证码，在监听中返回
    }
    //3 提交验证码
    private void submitCode(String country, String phone, String code){
        SMSSDK.submitVerificationCode(country, phone, code);//提交短信验证码，在监听中返回
    }
    //4 注销回调接口 registerEventHandler必须和unregisterEventHandler配套使用，否则可能造成内存泄漏。
    private void unregisterHandler(){
        SMSSDK.unregisterEventHandler(eh);
    //    Log.v("TAG", "注销回调接口");
    }
    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:             //验证码验证成功
                    //执行bmob注册
                    registerUser();
                    break;
                case 2:             //获取验证码成功,注意查看
                    ShowToas.showToast(RegisterActivity.this, "获取验证码成功,注意查看");
                    break;
                case 3:             //获取验证码失败,请填写正确的手机号码
                    ShowToas.showToast(RegisterActivity.this, "获取验证码失败,请填写正确的手机号码");
                    myCountDowmTimer.onFinish();
                    myCountDowmTimer.cancel();
                    break;
                case 4:             //验证码验证错误
                    ShowToas.showToast(RegisterActivity.this, "验证码错误");
                    break;
            }
        }
    };

}

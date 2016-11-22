package com.zt.txnews.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.bean.Invitation;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/9/20.
 * 资料编辑页
 */
public class MyMessageEditActivity extends Activity implements View.OnClickListener{
    private ImageView backImage;
    private TextView saveText,sexText,brithdayText,textcountText;
    private CircularImageView photoImage;
    private EditText nickNameEdit,mottoEdit;
    private RelativeLayout relativeLayout_Phtot;
    private LinearLayout linearLayout_sex,linearLayout_brithday;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymessageedit);
        initView();
        initViewData();
    }
    private void initView() {
        backImage = (ImageView) findViewById(R.id.edit_back_image);
        saveText = (TextView) findViewById(R.id.edit_save_modify_text);
        sexText = (TextView) findViewById(R.id.edit_sex_text);
        brithdayText = (TextView) findViewById(R.id.edit_birthday_text);
        textcountText = (TextView) findViewById(R.id.edit_Textcount_text);
        photoImage = (CircularImageView) findViewById(R.id.edit_photo_image);
        nickNameEdit = (EditText) findViewById(R.id.edit_nickname_Edit);
        mottoEdit = (EditText) findViewById(R.id.edit_motto_Edit);
        relativeLayout_Phtot = (RelativeLayout) findViewById(R.id.edit_photo_relative);
        linearLayout_sex = (LinearLayout) findViewById(R.id.edit_sex_linear);
        linearLayout_brithday = (LinearLayout) findViewById(R.id.edit_birthday_linear);
        backImage.setOnClickListener(this);
        saveText.setOnClickListener(this);
        relativeLayout_Phtot.setOnClickListener(this);
        linearLayout_sex.setOnClickListener(this);
        linearLayout_brithday.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("信息修改中...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressDialog.setCancelable(false);设置为false时 使用此方法才能实现按back键回调此方法
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    progressDialog.dismiss();
                }
                return false;
            }
        });
        mottoEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 16) {
                    Editable mo = mottoEdit.getText();
                    CharSequence ss = mo.subSequence(0, 16);//获取从0位置末（1）开始到第16个字[字母，数字，汉子 都是为1的长度]
                    mottoEdit.setText(ss);
                }
                //光标靠后
                mottoEdit.setSelection(mottoEdit.getText().length());
                nickNameEdit.setSelection(nickNameEdit.getText().length());
                //剩余可输入字符个数
                int len = 16 - s.length();
                textcountText.setText(len<=0?"0":len+"");
            }
        });

    }

    private User currentUser;
    private String sex="男";
    private void initViewData() {
        currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser==null){
            return;
        }
        if (currentUser.getIcon()==null){ //6.0系统
            photoImage.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
        }else{
            Picasso.with(this).load(currentUser.getIcon().getUrl()).into(photoImage);
        }
        nickNameEdit.setText(currentUser.getNickname());
        sexText.setText(currentUser.getSex());
        brithdayText.setText(currentUser.getBrithday());
        myBrithday = currentUser.getBrithday();
        mottoEdit.setText(currentUser.getMotto());
        sex = currentUser.getSex();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_back_image:
                finish();
                break;
            case R.id.edit_save_modify_text:
                if (currentUser==null){
                    ShowToas.showToast(this,"请先登录");
                    return;
                }else {
                    modifyMessage();
                }
                break;
            case R.id.edit_photo_relative:
                showPhotoSelectDialog();
                break;
            case R.id.edit_sex_linear:
                showSexSelectDialog();
                break;
            case R.id.edit_birthday_linear:
                showBrithdayDialog();
                break;
        }
    }

    //保存修改
    private String motto;
    private void modifyMessage() {
        String nickname = nickNameEdit.getText().toString().trim();
          motto = mottoEdit.getText().toString().trim();
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(motto)){
            ShowToas.showToast(this,"昵称或签名不能为空");
            return;
        }
        //当EidtText无焦点（focusable=false）时阻止输入法弹出
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(nickNameEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mottoEdit.getWindowToken(), 0);

        progressDialog.show();
        //判断是否修改过头像
        if (isuploadImage){
            modifyTextandImageMessage(nickname,sex,myBrithday,motto);
        }else {
            modifyTextMessage(nickname,sex,myBrithday,motto);
        }
    }
    /**
     * 修改 文本+图片
     */
    private void modifyTextandImageMessage(final String nickname, final String sex, final String brithday, final String motto){

        File picFile = new File(image_from_sd_paizhao_or_xianche__path);
        final BmobFile bmobFile = new BmobFile(picFile);

        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                User user = new User();
                user.setIcon(bmobFile);
                user.setNickname(nickname);
                user.setSex(sex);
                user.setBrithday(brithday);
                user.setMotto(motto);
                user.update(MyMessageEditActivity.this, currentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        //此时批量修改帖子表里的name和iconUrl字段【因为帖子大厅里的数据是从帖子表里获取的】
                        updateAllNameAndIconUrl(nickname,bmobFile.getUrl());
                        progressDialog.cancel();
                        ShowToas.showToast(MyMessageEditActivity.this, "信息保存成功");
                        finish();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.cancel();
                    }
                });
            }
            @Override
            public void onFailure(int i, String s) {
                if (i==9015){
                    ShowToas.showToast(MyMessageEditActivity.this,"服务连接超时");
                }
                if (i==9016){
                    ShowToas.showToast(MyMessageEditActivity.this,"网络连接异常，请检查你的网络连接");
                }
                progressDialog.cancel();
            }
        });

    }

    /**
     * 修改文本
     */
    private void modifyTextMessage(  final String nickname,   String sex,   String brithday,   String motto){
        final User user = new User();
        user.setNickname(nickname);
        user.setSex(sex);
        user.setBrithday(brithday);
        user.setMotto(motto);
        user.update(MyMessageEditActivity.this, currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                //只修改昵称name
                updateAllName(nickname);
                progressDialog.cancel();
                ShowToas.showToast(MyMessageEditActivity.this, "信息保存成功");
                finish();
            }
            @Override
            public void onFailure(int i, String s) {
                if (i==9016){
                    ShowToas.showToast(MyMessageEditActivity.this,"网络连接异常，请检查你的网络连接");
                }
                progressDialog.cancel();
            }
        });
    }

    private void showPhotoSelectDialog() {
        final Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.zdy_dailog, null);
        dialog.setTitle("头像来源");
        dialog.setContentView(view);
        TextView paizhaoText = (TextView)view.findViewById(R.id.text1);
        TextView fromxzheText = (TextView)view.findViewById(R.id.text2);
        paizhaoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                fromPaizhao();
            }
        });
        fromxzheText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                fromXianche();
            }
        });
        dialog.show();
    }
    /**
     * 打开系统相册or拍照，以后用到直接copy
     */

    private String image_from_sd_paizhao_or_xianche__path;//图片sd路径
    private boolean isuploadImage;//标记用户是否上传图片
    private String path;

    @TargetApi(Build.VERSION_CODES.M)
    private void fromPaizhao() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},22);//权限，请求码
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        path = Environment.getExternalStorageDirectory().getPath() + "/";
        //将当前的拍照时间作为图片的文件名称
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = simpleDateFormat.format(new Date()) + ".jpg";
        image_from_sd_paizhao_or_xianche__path = path + filename;
        File file = new File(image_from_sd_paizhao_or_xianche__path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//将图片文件转化为一个uri传入
        startActivityForResult(intent, 100); //打开相册
    }
    @TargetApi(Build.VERSION_CODES.M)
    private void fromXianche() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},33);
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 200);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100://拍照
                if (resultCode==this.RESULT_OK){
                    if(image_from_sd_paizhao_or_xianche__path!=null) {
                        //UIL框架加载本地sd卡图片路径为 String imageFilePath = "file://" + image_from_sd_paizhao;
                        //Picssao用file来封装文件
                        File file = new File(image_from_sd_paizhao_or_xianche__path);
                        Picasso.with(this).load(file).into(photoImage);
                        isuploadImage = true;
                    }
                }else{
                    ShowToas.showToast(this,"放弃拍照");
                }
                break;
            case 200://从相册
                if(resultCode==this.RESULT_OK) {
                    //内容解析者来操作内容提供最对数据的4方法
                    if (data!=null) {
                        Uri uri = data.getData();
                        if (uri!=null) {
                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                            //选择的就只是一张图片，所以cursor只有一条记录【注意：5.0系列的系统要选择本地图片 否则cursor就为null】
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    image_from_sd_paizhao_or_xianche__path = cursor.getString(cursor.getColumnIndex("_data"));//获取相册路径字段
                                    File file = new File(image_from_sd_paizhao_or_xianche__path);
                                    Picasso.with(this).load(file).into(photoImage);
                                    isuploadImage = true;
                                }
                            }
                        }
                    }
                }else{
                    ShowToas.showToast(this,"放弃从相册选择");
                }
                break;
        }
    }
    //性别设置
    private void showSexSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.sex_select_dialog, null);
        dialog.setView(view);
        dialog.setCancelable(true);
        RadioGroup radioGroup_man =(RadioGroup)view.findViewById(R.id.radioGroup_man);
        RadioGroup radioGroup_woman =(RadioGroup)view.findViewById(R.id.radioGroup_woman);
        RadioButton radio_man =(RadioButton) radioGroup_man.getChildAt(0);
        RadioButton radio_woman =(RadioButton) radioGroup_woman.getChildAt(0);
        TextView cancelText =(TextView) view.findViewById(R.id.cancel_text);
        RelativeLayout relativeLayout_man = (RelativeLayout) view.findViewById(R.id.sex_man_relative);
        RelativeLayout relativeLayout_woman = (RelativeLayout) view.findViewById(R.id.sex_woman_relative);
        if (sex!=null){
            if (sex.equals("男")){
                radio_man.setChecked(true);
            }else{
                radio_woman.setChecked(true);
            }
        }else{
            radio_man.setChecked(true);
        }
        relativeLayout_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "男";
                sexText.setText(sex);
                dialog.cancel();
            }
        });
        relativeLayout_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "女";
                sexText.setText(sex);
                dialog.cancel();
            }
        });
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    //生日设置
    private String myBrithday="2001-1-1";
    private void showBrithdayDialog() {
        DatePickerDialog datePicDailog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myBrithday = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                brithdayText.setText(myBrithday);
            }
        }, 1993, 1,1);
        datePicDailog.show();
    }
    //批量更新帖子表的name和iconUrl
    private void updateAllNameAndIconUrl(final String name, final String iconUrl){
        final List<BmobObject> invitations = new ArrayList<>();
        BmobQuery<Invitation> bmobQuery = new BmobQuery<>();

        bmobQuery.addWhereEqualTo("userId", currentUser.getObjectId());
        bmobQuery.setLimit(1000);

        bmobQuery.findObjects(this, new FindListener<Invitation>() {
            @Override
            public void onSuccess(List<Invitation> list) {
                if (list != null && list.size() > 0) {
                    for (Invitation invitation : list) {
                        invitation.setName(name);
                        invitation.setIconUrl(iconUrl);
                        invitations.add(invitation);
                    }
                    //更新字段
                    //第一种方式：v3.5.0之前的版本
                    new BmobObject().updateBatch(MyMessageEditActivity.this, invitations, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.v("TAG", "modify text+image success");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        //清除当前查询的缓存数据
        bmobQuery.clearCachedResult(this, Invitation.class);
    }
    //批量更新帖子表的name
    private void updateAllName(final String name){
        final List<BmobObject> invitations = new ArrayList<>();
        BmobQuery<Invitation> bmobQuery = new BmobQuery<>();

        bmobQuery.addWhereEqualTo("userId", currentUser.getObjectId());
        bmobQuery.setLimit(1000);

        bmobQuery.findObjects(this, new FindListener<Invitation>() {
            @Override
            public void onSuccess(List<Invitation> list) {
                if (list != null && list.size() > 0) {
                    for (Invitation invitation : list) {
                        invitation.setName(name);
                        invitations.add(invitation);
                    }
                    //更新字段
                    //第一种方式：v3.5.0之前的版本
                    new BmobObject().updateBatch(MyMessageEditActivity.this, invitations, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.v("TAG", "modify text success");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        //清除当前查询的缓存数据
        bmobQuery.clearCachedResult(this,Invitation.class);
    }

}

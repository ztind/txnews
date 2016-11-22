package com.zt.txnews.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.bean.Invitation;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/9/14.
 */
public class SendInvitationActivity extends Activity implements View.OnClickListener{
    private EditText editTextTitle, editTextContent;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendforum);
        initView();
    }

    private void initView() {
        editTextTitle = (EditText) findViewById(R.id.ft_title);
        editTextContent = (EditText) findViewById(R.id.ft_content);
        imageView = (ImageView) findViewById(R.id.ft_iamge);
        imageView.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在发表请稍等...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode==event.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
                    progressDialog.dismiss();
                }
                return false;
            }
        });
    }

    private String title,content;
    private User currentUser;
    public void sendinvitationClick(View view) {
           title = editTextTitle.getText().toString().trim();
           content = editTextContent.getText().toString();
           currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser==null){
            ShowToas.showToast(this,"请先登陆");
            return;
        }
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)){
            ShowToas.showToast(this,"发表的帖子不能为空哦");
            return;
        }
        //判断用户发表的帖子是否带图片
        progressDialog.show();
        if(isuploadImage) {
            uploadTextWithImage();
        }else {
            uplaodText();
        }

    }
    public void onCloseClick(View view){
        finish();
    }
    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("图片来自于").
                setCancelable(true).
                setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fromPaizhao();
                    }
                }).setNegativeButton("从相册选择本地图片", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fromXianche();
            }
        }).show();
    }

    /**
     * 打开系统相册or拍照，以后用到直接copy
     */

    private String image_from_sd_paizhao_or_xianche__path;//图片sd路径
    private boolean isuploadImage;//标记用户是否上传图片
    private String path;

    private void fromPaizhao() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //注意：6.0的系统存储不到sdcard路径下
        path = Environment.getExternalStorageDirectory().getPath() + "/";
        //将当前的拍照时间作为图片的文件名称
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = simpleDateFormat.format(new Date()) + ".jpg";
        image_from_sd_paizhao_or_xianche__path = path + filename;
        File file = new File(image_from_sd_paizhao_or_xianche__path);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));//将图片文件转化为一个uri传入
        startActivityForResult(intent, 100); //打开相册

    }
    private void fromXianche() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,200);
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
                            Picasso.with(this).load(file).into(imageView);
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
                                    Picasso.with(this).load(file).into(imageView);
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
    /**
     * 上传 文本+图片
     */
    private void uploadTextWithImage(){

        File picFile = new File(image_from_sd_paizhao_or_xianche__path);
        final BmobFile bmobFile = new BmobFile(picFile);

        bmobFile.uploadblock(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                //创建帖子对象
                Invitation invitation = new Invitation();

                invitation.setName(currentUser.getNickname());
                invitation.setTitle(title);
                invitation.setContent(content);
                invitation.setIconUrl(currentUser.getIcon().getUrl());
                invitation.setUserId(currentUser.getObjectId());
                invitation.setImage(bmobFile);
                invitation.setType("文本+图片");
                invitation.setDianzangCount(0);
                //上传帖子
                invitation.save(SendInvitationActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        ShowToas.showToast(SendInvitationActivity.this, "帖子发表成功");
                        editTextTitle.setText(null);
                        editTextContent.setText(null);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
                        isuploadImage = false;
                        progressDialog.dismiss();
                        finish();
                    }
                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Log.v("TAG", i + "***text+image***" + s);
                progressDialog.dismiss();
            }
        });
    }
    /**
     * 上传 文本
     */
    private void uplaodText(){
        //创建帖子对象
        Invitation invitation = new Invitation();

        invitation.setName(currentUser.getNickname());
        invitation.setTitle(title);
        invitation.setContent(content);
        invitation.setIconUrl(currentUser.getIcon().getUrl());
        invitation.setUserId(currentUser.getObjectId());
        invitation.setType("文本");
        invitation.setDianzangCount(0);
        //上传帖子
        invitation.save(SendInvitationActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                ShowToas.showToast(SendInvitationActivity.this, "帖子发表成功");
                editTextTitle.setText(null);
                editTextContent.setText(null);
                progressDialog.dismiss();
                finish();
            }
            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
            }
        });
    }
}

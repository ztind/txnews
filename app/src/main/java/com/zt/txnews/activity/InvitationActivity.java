package com.zt.txnews.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.adapter.CommentAdapter;
import com.zt.txnews.bean.Comment;
import com.zt.txnews.bean.Invitation;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Administrator on 2016/9/15.
 * 帖子详情页:帖子点赞数是点击帖子item的Invitation传递过来的（当对评论点赞时，可设置之前listview里的该item的invitation的点赞数加1【使用请求吗和结果码来实现】
 * ，下次进入时就实现了点赞+1），而评论的点赞数是进入这个界面时联网从bmob段获取
 */
public class InvitationActivity extends Activity implements View.OnClickListener{
    private CircularImageView photo;
    private ImageView dianzhanImage,commentImage;
    private ImageView image;
    private TextView nameText,titleText,contentText,dianzhanCount;
    private PullToRefreshListView pullToRefreshListView;
    private EditText editText;
    private Button sendBut;
    private RelativeLayout relativeLayout;
    private CommentAdapter adapter;

    private RelativeLayout relativeLayout_showImage_back;
    private ImageView clickToshowImage,shareImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        initData();
        initView();
        initCommentListDateFromBmob();
    }

    private String photoUrl;
    private String name;
    private String title;
    private String content;
    private String showImageUrl;
    private Picasso picasso;
    private int zhangCount;
    private Invitation invitationFriend;
    private void initData() {
        invitationFriend = (Invitation) getIntent().getSerializableExtra("invitation");
        photoUrl = invitationFriend.getIconUrl();
        name = invitationFriend.getName();
        title = invitationFriend.getTitle();
        content = invitationFriend.getContent();
        zhangCount = invitationFriend.getDianzangCount();

        BmobFile bmobfile = invitationFriend.getImage();
        if (bmobfile!=null) {
            showImageUrl = bmobfile.getUrl();
        }
        picasso = Picasso.with(this);

    }

    private void initView() {
        relativeLayout_showImage_back = (RelativeLayout) findViewById(R.id.relative_showimage_background);
        clickToshowImage = (ImageView) findViewById(R.id.click_show_image);

        photo = (CircularImageView) findViewById(R.id.invitation_photo);
        nameText = (TextView) findViewById(R.id.invitation_name);

        editText = (EditText) findViewById(R.id.invitation_edit);
        sendBut = (Button) findViewById(R.id.invitation_button_send);
        relativeLayout = (RelativeLayout) findViewById(R.id.invi_rela);
        editText.addTextChangedListener(new MyEditWatch());
        sendBut.setOnClickListener(this);

        //获取pullToRefreshListView列表头部视图
        View pulllistview_top = LayoutInflater.from(this).inflate(R.layout.invi_pulltorefreshlistview_top, null);
        titleText = (TextView) pulllistview_top.findViewById(R.id.invitation_title);
        contentText = (TextView) pulllistview_top.findViewById(R.id.invitation_content);
        image = (ImageView) pulllistview_top.findViewById(R.id.invitation_showimage);
        dianzhanImage = (ImageView) pulllistview_top.findViewById(R.id.image_dianzhang);
        dianzhanCount = (TextView) pulllistview_top.findViewById(R.id.text_count);
        commentImage = (ImageView) pulllistview_top.findViewById(R.id.image_comment);
        shareImage = (ImageView) pulllistview_top.findViewById(R.id.image_share);
        dianzhanImage.setOnClickListener(this);
        commentImage.setOnClickListener(this);
        shareImage.setOnClickListener(this);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picasso.load(showImageUrl).into(clickToshowImage);
                relativeLayout_showImage_back.setVisibility(View.VISIBLE);
            }
        });
        relativeLayout_showImage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout_showImage_back.setVisibility(View.GONE);
            }
        });

        //初始化pullToRefreshListView
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.invitation_pulllistview);

        //添加到头部
        pullToRefreshListView.getRefreshableView().addHeaderView(pulllistview_top);

        pullToRefreshListView.setMode(PullToRefreshListView.Mode.PULL_UP_TO_REFRESH);

        ILoadingLayout proxy = pullToRefreshListView.getLoadingLayoutProxy(false, true);
        proxy.setPullLabel("上拉加载...");
        proxy.setRefreshingLabel("正在玩命加载中...");
        proxy.setReleaseLabel("放开加载...");

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上啦刷新评论
                if (commentList!=null && commentList.size()>0){   ////zhongyao
                    startpoint += 8;  //从bmob端第一条记录开始叠加
                }
                initCommentListDateFromBmob();
            }
        });

        //初始化Ui数据 [****************注意:当listview/pullToRefreshListView里没有item时其头部视图不会显示出来*****************]
        picasso.load(photoUrl).into(photo);
        nameText.setText(name);
        titleText.setText("标题：" + title);
        contentText.setText("    " + content);
        dianzhanCount.setText(zhangCount + "");

        if (showImageUrl != null) {
//          mImageSetting();//用流的方法加载图片形成缩略图会发生oom异常（弃用）

            //picssao的自我裁剪方法resize（width,heigth）当有网络时picssao就会让其加载过的图片显示出来
            picasso.load(showImageUrl).resize(getPingmu().widthPixels/2,getPingmu().heightPixels/3).centerCrop().into(image);
        }

    }


    public void onclickBack(View view){
        finish();
    }
    public void onGoPersonCenterClick(View view) {
        User curentUser = BmobUser.getCurrentUser(this, User.class);
        if (curentUser!=null){
            if(invitationFriend.getUserId().equals(curentUser.getObjectId())){
                startActivity(new Intent(this,MyCenterActivity.class));
            }else{
                Intent intent = new Intent(this, FriendsActivity.class);
                intent.putExtra("invitationfriend", invitationFriend);
                startActivity(intent);
            }
        }else {
            ShowToas.showToast(this, "请先登陆");
        }

    }

    private int clickTime;
    private int result_zan_count;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_dianzhang://点赞图片

                if (clickTime==0) {  //保证在当前界面只能点赞一次
                    clickTime = 1;
                    //更新bmob帖子表的、该记录的count字段(设置不支持取消)
                    final Invitation invitation = new Invitation();
                    invitation.setDianzangCount(zhangCount+1);
                    invitation.update(this, invitationFriend.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            dianzhanImage.setImageDrawable(getResources().getDrawable(R.mipmap.zhan_press));
                            dianzhanCount.setText((zhangCount)+1+"");
                            //invitationFriend的点赞数+1
                            result_zan_count = invitationFriend.getDianzangCount() + 1;
                        }
                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
                break;
            case R.id.image_comment:
                relativeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.invitation_button_send:
                sendCommentToInvitation();
                break;
            case R.id.image_share:
                User user = BmobUser.getCurrentUser(this, User.class);
                if (user==null) {
                    ShowToas.showToast(this, "请先登陆");
                    return;
                }
                showShare();
                break;
        }
    }
    private void showShare() {

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle("新闻头条");

        oks.setText(invitationFriend.getContent());

        oks.setComment(invitationFriend.getContent());
        if(invitationFriend.getImage()!=null){
            oks.setImageUrl(invitationFriend.getImage().getUrl());
        }
        oks.setSite(getString(R.string.app_name));

        // 启动分享GUI
        oks.show(this);
    }
    private DisplayMetrics getPingmu(){
        //获取手机屏幕的高 来动态设置图形背景图片的高度位置从而适应不同的屏幕大小
        WindowManager mag = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mag.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    private List<Comment> commentList = new ArrayList<>();
    //对帖子的评论
    private void sendCommentToInvitation() {
        User curentUser = BmobUser.getCurrentUser(this, User.class);
        if (curentUser==null){
            ShowToas.showToast(this,"登陆后方可评论");
            return;
        }
        String commentContent = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(commentContent)) {
            final Comment comment = new Comment();
            //对某人某帖子评论
            comment.setUserId(invitationFriend.getUserId());
            comment.setInvitationId(invitationFriend.getObjectId());
            //评论内容
            comment.setCommentContent(commentContent);
            comment.setName(curentUser.getNickname());
            comment.setIconUrl(curentUser.getIcon().getUrl());
            comment.setTowho(invitationFriend.getName());
            comment.setToTitle(invitationFriend.getTitle());
            comment.setDianzangCount(0);
            comment.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    commentList.add(comment);
                    showCommentInListview();
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }
    //对评论的回复
    private void sendReplyToComment(){

    }
    //对回复的回复
    private void senReplyToreply(){

    }

    //将发送评论显示显示到pullToRefreshListView列表里（无需从bmob获取）
    private int ff;
    private void showCommentInListview() {
        if (ff==0){  //保证只设置一次adapter
            adapter = new CommentAdapter(this, commentList);
            pullToRefreshListView.setAdapter(adapter);
            ff = 1;
        }
        adapter.notifyDataSetChanged();
        //添加完后显示列表最后一个item
        pullToRefreshListView.getRefreshableView().setSelection(adapter.getCount());
        editText.setText(null);
        relativeLayout.setVisibility(View.GONE);
        //当EidtText无焦点（focusable=false）时阻止输入法弹出
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    //从bmob获取评论信息,初始化评论列表
    private BmobQuery<Comment> bmobQuery = new BmobQuery<>();
    private int limit=8;
    private int startpoint=0;

    private void initCommentListDateFromBmob() {
        bmobQuery.addWhereEqualTo("userId", invitationFriend.getUserId());
        bmobQuery.addWhereEqualTo("invitationId", invitationFriend.getObjectId());

        bmobQuery.setSkip(startpoint);
        bmobQuery.setLimit(limit);

        bmobQuery.findObjects(this, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                if(commentList.size()==0) {
                    commentList = list;  //并且将获取的集合数据赋给commentList
                    adapter = new CommentAdapter(InvitationActivity.this, list); //保证只设置一次adapter,从而改变list的大小，notifyDataSetChanged()即可刷新
                    pullToRefreshListView.setAdapter(adapter);
                    ff = 1;
                    adapter.notifyDataSetChanged();
                }else{
                    for (Comment comment:list) {
                        commentList.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                }
                if (pullToRefreshListView.isShown()){
                    pullToRefreshListView.onRefreshComplete();
                }
            }
            @Override
            public void onError(int i, String s) {
                if (pullToRefreshListView.isShown()){
                    pullToRefreshListView.onRefreshComplete();
                }
            }
        });
    }
    //生成缩略图来适配屏幕
    private void mImageSetting(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(getPingmu().widthPixels/2, getPingmu().heightPixels / 3);
                InputStream is = null;
                BufferedInputStream bis = null;
                try {
                    URL url = new URL(showImageUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                     is = httpURLConnection.getInputStream();
                     bis = new BufferedInputStream(is);
                    //1、首先加载要操作的图片
                    Bitmap bitmap = BitmapFactory.decodeStream(bis);//oom异常

                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();

                    int mHeight = lps.height;
                    float mWidth = lps.width;

                    float scaleHeight = ((float) mHeight) / height;
                    float scaleWidth = ((float) mWidth) / width;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);

                    resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);

                    myHandler.sendEmptyMessage(222);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (bis!=null){
                        try {
                            bis.close();
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
            }
        }).start();
    }

    private Bitmap resizedBitmap;
    private boolean showinatyImageisShow;
    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==222){
                image.setImageBitmap(resizedBitmap);
                showinatyImageisShow = true;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            //点击返回键时返回点赞数
            if (result_zan_count>0){
                Intent intent = new Intent();
                intent.putExtra("result_count", result_zan_count);
                setResult(999, intent);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    class MyEditWatch implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
             if (s.length()>0){
                 sendBut.setPressed(true);
             }else{
                 sendBut.setPressed(false);
             }
        }
    }
}

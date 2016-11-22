package com.zt.txnews.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.adapter.FriendsVpAdapter;
import com.zt.txnews.bean.Friends;
import com.zt.txnews.bean.Invitation;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/9/19.
 * 好友或路人界面
 */
public class FriendsActivity extends FragmentActivity implements View.OnClickListener {
    private CircularImageView circularImageView_photo;
    private TextView nameText;
    private TextView mottoText;
    private Button addButton;
    private ViewPager viewPager;
    private FriendsVpAdapter friendsVpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firendsorluren);
        initView();
        initViewData();
    }

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private void initView() {
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
         mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.f_collapsing_toolbar_layout);
//        mCollapsingToolbarLayout.setTitle("我的书签");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);//设置还没收缩时状态下字体颜色(TRANSPARENT透明)
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.RED);//设置收缩后Toolbar上字体的颜色

        Toolbar toobar = (Toolbar) findViewById(R.id.f_mycenter_toobar);
        toobar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendsActivity.this.finish();
            }
        });
        circularImageView_photo = (CircularImageView) findViewById(R.id.f_mycenter_photo);
        nameText = (TextView) findViewById(R.id.f_mycenter_name);
        mottoText = (TextView) findViewById(R.id.f_mycenter_motto);
        addButton = (Button) findViewById(R.id.f_addguanzhu_button);
        viewPager = (ViewPager) findViewById(R.id.f_mycenter_viewpager);
        addButton.setOnClickListener(this);
    }

    private Invitation invitationfriend;
    private void initViewData() {
        invitationfriend = (Invitation) getIntent().getExtras().getSerializable("invitationfriend");
        String iconUrl = invitationfriend.getIconUrl();
        String name = invitationfriend.getName();
        String userId = invitationfriend.getUserId();

        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(this, userId, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                mottoText.setText(user.getMotto());
            }
            @Override
            public void onFailure(int i, String s) {

            }
        });
        Picasso.with(this).load(iconUrl).into(circularImageView_photo);
        nameText.setText(name);
        mCollapsingToolbarLayout.setTitle(name);
        //button is guanzhu
        currentUser = BmobUser.getCurrentUser(this, User.class);
        BmobQuery<Friends> bmobQuery1 = new BmobQuery<>();
        bmobQuery1.addWhereEqualTo("meId", currentUser.getObjectId());
        bmobQuery1.setLimit(1000);
        bmobQuery1.findObjects(this, new FindListener<Friends>() {
            @Override
            public void onSuccess(List<Friends> list) {
                if (list != null && list.size() > 0) {
                    //关注的好友的数量是多个的....[for循环遍历当前贴友的id是否在此集合里，若在则按显示“已关注”]
                    int hh=0;
                    for (Friends friends :list){
                        if (friends.getFriendsId().equals(invitationfriend.getUserId())){
                            hh=1;
                            break;
                        }
                    }
                    if (hh==1){
                        addButton.setText("已关注");
                        addButton.setClickable(false);
                    }else{
                        addButton.setText("+关注");
                        addButton.setClickable(true);
                    }
                }else{
                    addButton.setText("+关注");
                    addButton.setClickable(true);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

        //viewpager data init
        friendsVpAdapter = new FriendsVpAdapter(getSupportFragmentManager());
        viewPager.setAdapter(friendsVpAdapter);

    }
    //fragment的onAttch()方法加载完成后回调此方法
    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        // interface is null
        if (interfaceInvitationListener!=null){
            interfaceInvitationListener.setInterfaceInvitationListener(invitationfriend);
        }
    }


    private User currentUser;
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.f_addguanzhu_button) {
            Friends friends = new Friends();
            friends.setMeId(currentUser.getObjectId());
            friends.setMeName(currentUser.getNickname());
            friends.setFriendsId(invitationfriend.getUserId());
            friends.setFriendsName(invitationfriend.getName());
            friends.setFriendPhotoIcon(invitationfriend.getIconUrl());
            friends.setIsGuanzhu(true);
            friends.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    ShowToas.showToast(FriendsActivity.this,"关注成功");
                    addButton.setText("已关注");
                    addButton.setClickable(false);
                }
                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }
    public interface interfaceInvitationListener{
        void setInterfaceInvitationListener(Invitation invitation);
    }

    public interfaceInvitationListener interfaceInvitationListener;

    public void setInterfaceInvitationListener(interfaceInvitationListener interfaceInvitationListener) {
        this.interfaceInvitationListener = interfaceInvitationListener;
    }
}

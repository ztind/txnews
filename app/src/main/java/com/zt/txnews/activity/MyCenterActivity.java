package com.zt.txnews.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.adapter.MyCenterVpAdapter;
import com.zt.txnews.bean.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/9/14.
 * 个人主页
 */
public class MyCenterActivity extends FragmentActivity implements View.OnClickListener{
    private TextView editMessageText;
    private CircularImageView circularImageView_photo;
    private TextView nameText, mottoText;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> titleList;
    private MyCenterVpAdapter myCenterVpAdapter;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycenter);
        initData();
        initView();
    }

    private void initData() {
        titleList = new ArrayList<>();
        titleList.add(0,"我的帖子");
        titleList.add(1,"关注好友");
    }

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    private void initView() {
        //使用CollapsingToolbarLayout必须把title设置到CollapsingToolbarLayout上，设置到Toolbar上则不会显示
         mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
//        mCollapsingToolbarLayout.setTitle("我的书签");
        //通过CollapsingToolbarLayout修改字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);//设置还没收缩时状态下字体颜色(TRANSPARENT透明)
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.RED);//设置收缩后Toolbar上字体的颜色

        Toolbar toobar = (Toolbar) findViewById(R.id.mycenter_toobar);
        toobar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCenterActivity.this.finish();
            }
        });
        editMessageText = (TextView) findViewById(R.id.mycenter_editmessage_text);
        circularImageView_photo = (CircularImageView) findViewById(R.id.mycenter_photo);
        nameText = (TextView) findViewById(R.id.mycenter_name);
        mottoText = (TextView) findViewById(R.id.mycenter_motto);
        tabLayout = (TabLayout) findViewById(R.id.mycenter_tablayout);
        viewPager = (ViewPager) findViewById(R.id.mycenter_viewpager);
        editMessageText.setOnClickListener(this);
        myCenterVpAdapter = new MyCenterVpAdapter(getSupportFragmentManager(), titleList);
        viewPager.setAdapter(myCenterVpAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //fab
        fab = (FloatingActionButton) findViewById(R.id.mycenter_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyCenterActivity.this,SendInvitationActivity.class));
            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.mycenter_editmessage_text){
            startActivity(new Intent(this,MyMessageEditActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判读是否登陆
        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser!=null){
            if (currentUser.getIcon()==null){ //6.0系统
                circularImageView_photo.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
            }else{
                Picasso.with(this).load(currentUser.getIcon().getUrl()).into(circularImageView_photo);
            }
            nameText.setText(currentUser.getNickname());
            mottoText.setText(currentUser.getMotto());
            mCollapsingToolbarLayout.setTitle(currentUser.getNickname());
        }
    }
}

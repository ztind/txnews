package com.zt.txnews.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.zt.jackone.AppConnect;
import com.zt.txnews.R;
import com.zt.txnews.adapter.VpAdapter;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * creat by 2016/9/9
 */

public class MainActivity extends FragmentActivity implements MenuItem.OnMenuItemClickListener{
    private List<String> categoryList;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VpAdapter vpAdapter;
    private DrawerLayout drawerLayout;//抽屉盒子
    private NavigationView navigationView;//导航视图
    private CircularImageView mycion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitleData();
        initView();
    }

    private void initTitleData() {
        categoryList = new ArrayList<>();
        categoryList.add("头条");
        categoryList.add("社会");
        categoryList.add("娱乐");
        categoryList.add("国际");
        categoryList.add("科技");
        categoryList.add("体育");
        categoryList.add("军事");
        categoryList.add("国内");
        categoryList.add("财经");
        categoryList.add("时尚");

    }
    private void initView() {
        AppConnect.getInstance(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        navigationView = (NavigationView) findViewById(R.id.id_design_navigation_view);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new VpAdapter(getSupportFragmentManager(), categoryList);
        viewPager.setAdapter(vpAdapter);
        //程序启动后会自动加载fragemnt1 和fragment2点击和滑动都会执行相应fragment的onCreateView()方法so fragment布局和fragment都单列设计.然后在各自的fragment实现下拉刷新
        tabLayout.setupWithViewPager(viewPager);

        //tablayout设置
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        mycion = (CircularImageView) findViewById(R.id.myicon);
        mycion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //获取header视图
        View headerview = navigationView.getHeaderView(0);
        userHead = (CircularImageView) headerview.findViewById(R.id.user_head);
        userNickName = (TextView) headerview.findViewById(R.id.user_nickname);
        userMotto = (TextView) headerview.findViewById(R.id.user_motto);
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bmob API来判断是否已经登陆
                drawerLayout.closeDrawers();
                User bmobUser = BmobUser.getCurrentUser(MainActivity.this, User.class);//获取缓存用户
                if (bmobUser != null) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, MyCenterActivity.class));
                } else {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });
        //menu
        Menu menu = navigationView.getMenu();
        //item
        MenuItem luentan = menu.findItem(R.id.item_luentan);
        MenuItem myCenter = menu.findItem(R.id.item_mycenter);
        MenuItem setting = menu.findItem(R.id.item_setting);
        MenuItem yuletueijing = menu.findItem(R.id.item_yule);
        MenuItem exit = menu.findItem(R.id.item_exit);

        luentan.setOnMenuItemClickListener(this);
        myCenter.setOnMenuItemClickListener(this);
        setting.setOnMenuItemClickListener(this);
        yuletueijing.setOnMenuItemClickListener(this);
        exit.setOnMenuItemClickListener(this);

    }
    private CircularImageView userHead;
    private TextView userNickName;
    private TextView userMotto;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        //获取登录后的头像
        User bmobUser = BmobUser.getCurrentUser(this, User.class);//获取缓存用户
        if(bmobUser!=null) {
            //6.0
            if(Build.VERSION.SDK_INT>=23) {
//        自V3.4.5版本开始，SDK新增了getObjectByKey(context,key)方法从本地缓存中获取当前登陆用户某一列的值。其中key为用户表的指定列名。
                JSONObject json = (JSONObject) BmobUser.getObjectByKey(this, "icon");
                if(json==null){
                    mycion.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
                    userHead.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
                    userNickName.setText(bmobUser.getNickname());
                    userMotto.setText(bmobUser.getMotto());
                }else{  //修改头像后 icon不为null
                    try {
                        String icon_url = json.getString("url");
                        Picasso.with(this).load(icon_url).into(mycion);
                        Picasso.with(this).load(icon_url).into(userHead);
                        userNickName.setText(bmobUser.getNickname());
                        userMotto.setText(bmobUser.getMotto());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }

            //system<6.0
            JSONObject json = (JSONObject) BmobUser.getObjectByKey(this, "icon");//或 bmobUser.getIcon().getUrl()

            //6.0以下的手机系统登录 6.0注册的账号(头像未修改时 bmob端的icon字段为null)
            if(json==null){
                mycion.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
                userHead.setImageDrawable(getResources().getDrawable(R.drawable.defaulticon));
                userNickName.setText(bmobUser.getNickname());
                userMotto.setText(bmobUser.getMotto());
                return;
            }

            try {
                String icon_url = json.getString("url");
                Picasso.with(this).load(icon_url).into(mycion);
                Picasso.with(this).load(icon_url).into(userHead);
                userNickName.setText(bmobUser.getNickname());
                userMotto.setText(bmobUser.getMotto());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //未登陆头像
            mycion.setImageDrawable(getResources().getDrawable(R.drawable.icon));
            userHead.setImageDrawable(getResources().getDrawable(R.drawable.icon));
            userNickName.setText("点击头像登陆");
            userMotto.setText("***我的个性签名***");
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_luentan:
                startActivity(new Intent(this,ForumActivity.class));//显示意图跳转
                break;
            case R.id.item_mycenter:
                startActivity(new Intent(this, MyCenterActivity.class));
                break;
            case R.id.item_setting:
                startActivity(new Intent(this,SystemSettingActivity.class));
                break;
            case R.id.item_yule:
                AppConnect.getInstance(this).showAppOffers(this);
                break;
            case R.id.item_exit:
                finish();
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConnect.getInstance(this).close();
    }

    private boolean isBack=false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==event.KEYCODE_BACK){
            if (!isBack) {
                isBack = true;
                ShowToas.showToast(this, "再按一次退出");
                myBackHandler.sendEmptyMessageDelayed(110, 3000);
                return false;
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private Handler myBackHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==110) {
                isBack = false;
            }
        }
    };
}

package com.zt.txnews.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zt.txnews.R;
import com.zt.txnews.adapter.ForumListAdapter;
import com.zt.txnews.bean.Invitation;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by zt on 2016/9/14
 * 欢笑论坛 缓存策略
 */
public class ForumActivity extends Activity implements View.OnClickListener{
    private PullToRefreshListView pullToRefreshListView;
    private ProgressDialog progressDialog;
    private ForumListAdapter forumListAdapter;
    private BmobQuery<Invitation> bmobQuery;
    private LinearLayout linearLayout_network_msg;
    private Button refreshBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        initView();
    }
    private int currentClickListItem; //当前被点击的list集合的position

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("数据加载中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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

        linearLayout_network_msg = (LinearLayout) findViewById(R.id.forum_network_msg_linear);
        refreshBut = (Button) findViewById(R.id.forum_refresh_but);
        refreshBut.setOnClickListener(this);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.forum_listview);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);//从网络获取
                quaryInvitationsFromBmob();
                //显示列表最后一个item
                pullToRefreshListView.getRefreshableView().setSelection(forumListAdapter.getCount());
            }
        });
        pullToRefreshListView.setVisibility(View.VISIBLE);
        //设置上啦提示文本
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);//写在设置之前

        ILoadingLayout proxy2 = pullToRefreshListView.getLoadingLayoutProxy(false,true);
        proxy2.setPullLabel("上拉加载...");
        proxy2.setRefreshingLabel("正在玩命加载中...");
        proxy2.setReleaseLabel("放开加载...");
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentClickListItem = position - 1;
                Invitation invitation = (Invitation) forumListAdapter.getItem(position - 1);
                Intent intent = new Intent(ForumActivity.this, InvitationActivity.class);
                intent.putExtra("invitation", invitation);
                startActivityForResult(intent, 888);
            }
        });


        //*****初始化查询器
        bmobQuery = new BmobQuery<Invitation>();
        bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天);

        //第一次进入应用的时候,设置其查询的缓存策略为CACHE_ELSE_NETWORK,当用户执行上拉或者下拉刷新操作时设置查询的缓存策略为NETWORK_ELSE_CACHE。

        // //判断网络是否连接
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info!=null){
            if(info.isAvailable()){
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }else{
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
            }
        }else{
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        }
        //获取帖子列表数据
        quaryInvitationsFromBmob();

    }
    private int limit=10;

    private List<Invitation> list;
    private void quaryInvitationsFromBmob() {
        bmobQuery.groupby(new String[]{"createdAt"});// 按照帖子创建时间分组
        bmobQuery.order("-createdAt");// 降序排列

        bmobQuery.setSkip(list==null?0:list.size());//跳过tg条查询
        bmobQuery.setLimit(limit); //每次查询limit条记录
        //无网络时从缓存获取数据也会调用此回调接口哦(无缓存走onError i=9009 s=No cache data，有缓存走onSuccess初始化列表数据)
        bmobQuery.findObjects(this, new FindListener<Invitation>() {
            @Override
            public void onSuccess(List<Invitation> list) {

                if (ForumActivity.this.list==null) {
                    ForumActivity.this.list = list;//赋值给当前的集合，
                    forumListAdapter = new ForumListAdapter(ForumActivity.this, list);
                    pullToRefreshListView.setAdapter(forumListAdapter);
                    forumListAdapter.notifyDataSetChanged();//只设置一次adapter的情况适用
                }else{
                    for (Invitation invitation:list) {
                        ForumActivity.this.list.add(invitation);//添加到集合中
                    }
                    forumListAdapter.notifyDataSetChanged();//只设置一次adapter的情况适用
                }
                if (pullToRefreshListView.isShown()) {
                    pullToRefreshListView.onRefreshComplete();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
                linearLayout_network_msg.setVisibility(View.GONE);
            }
            @Override
            public void onError(int i, String s) {
                if (pullToRefreshListView.isShown()){
                    pullToRefreshListView.onRefreshComplete();
                }
                if (progressDialog.isShowing()){
                    progressDialog.cancel();
                }
//               if(i==9016){
//                   ShowToas.showToast(ForumActivity.this, "网络连接异常，请检查你的网络连接");
//               }
               if(list==null){
                   linearLayout_network_msg.setVisibility(View.VISIBLE);
               }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.forum_refresh_but) { //刷新
            quaryInvitationsFromBmob();
        }
    }

    //接收返回后相加的点赞数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==888 && resultCode==999) {
            int result_count = data.getIntExtra("result_count", 1);
            list.get(currentClickListItem).setDianzangCount(result_count);
        }
    }
    public void onbackClick(View view){
        finish();
    }
}

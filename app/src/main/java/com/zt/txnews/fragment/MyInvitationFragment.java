package com.zt.txnews.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zt.txnews.R;
import com.zt.txnews.activity.InvitationActivity;
import com.zt.txnews.adapter.Fragment1PulltoRefrehLiatAdapter;
import com.zt.txnews.bean.Invitation;
import com.zt.txnews.bean.User;
import com.zt.txnews.utils.ShowToas;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/9/18.
 * Fragment和xml布局的单列设计模式化:  but考虑到在发表帖子成功后返回到此界面时程序崩溃，所以就不要设置为静态类了（或 根据error消息public化构造方法）
 */
public class MyInvitationFragment extends Fragment {
    private static MyInvitationFragment instance;
    private Fragment1PulltoRefrehLiatAdapter fragment1PulltoRefrehLiatAdapter;
    public MyInvitationFragment() {
    }

    private static View view;
    private PullToRefreshListView pullToRefreshListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_1, container, false);
            initView();
            return view;
        }
        return view;
    }

    public static MyInvitationFragment getInstance() {
        if (instance == null) {
            instance = new MyInvitationFragment();
            return instance;
        }
        return instance;
    }

    private void initView() {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.fragment1_pulltorefreshlistview);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                if (currentUser == null) {
                    return;
                }
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                initData();
            }
        });
        //上啦加载更多
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        ILoadingLayout proxy2 = pullToRefreshListView.getLoadingLayoutProxy(false, true);
        proxy2.setPullLabel("上拉加载...");
        proxy2.setRefreshingLabel("正在玩命加载中...");
        proxy2.setReleaseLabel("放开加载...");
        //item的点击事件
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fragment1PulltoRefrehLiatAdapter!=null) {
                    currentClickListItem = position - 1;
                    Invitation invitation = (Invitation) fragment1PulltoRefrehLiatAdapter.getItem(position - 1);
                    Intent intent = new Intent(getActivity(), InvitationActivity.class);
                    intent.putExtra("invitation", invitation);
                    startActivityForResult(intent, 777);
                }
            }
        });
        //item长按delete
        pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Invitation invitation = (Invitation) fragment1PulltoRefrehLiatAdapter.getItem(position - 1);
                deleteItem(invitation.getObjectId(),position-1);

                return false;
            }
        });
        //init Data
        currentUser = BmobUser.getCurrentUser(getActivity(), User.class);
        if (currentUser==null){
            return;
        }
        bmobQuery = new BmobQuery<>(); //创建查询器
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);

        initData();
    }

    private  User currentUser;
    private int limit = 10; //每次查询几条

    private BmobQuery<Invitation> bmobQuery;
    private List<Invitation> invitationsList;
    private void initData() {
        //setSKip方法可以做到跳过查询的前多少条数据来实现分页查询的功能。默认情况下Skip的值为10。
        bmobQuery.setSkip(invitationsList==null?0:invitationsList.size());

        bmobQuery.setLimit(limit);
        bmobQuery.addWhereEqualTo("userId", currentUser.getObjectId());
//        bmobQuery.groupby(new String[]{"createdAt"});// 按照帖子创建时间分组
        bmobQuery.order("-createdAt");// 降序排列

        bmobQuery.findObjects(getActivity(), new FindListener<Invitation>() {
            @Override
            public void onSuccess(List<Invitation> list) {
                //保证只设置一次adapter 用notifyDataSetChanged()刷新{设置一次adapter,list一个}
                if (invitationsList == null) {
                    invitationsList = list;
                    fragment1PulltoRefrehLiatAdapter = new Fragment1PulltoRefrehLiatAdapter(getActivity(), list);
                    pullToRefreshListView.setAdapter(fragment1PulltoRefrehLiatAdapter);
                    fragment1PulltoRefrehLiatAdapter.notifyDataSetChanged();
                } else {
                    for (Invitation invitation : list) {
                        invitationsList.add(invitation);//之后的数据直接添加后，用notifyDataSetChanged()即可实现列表刷新
                    }
                    fragment1PulltoRefrehLiatAdapter.notifyDataSetChanged();
                }
                pullToRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(int i, String s) {
                pullToRefreshListView.onRefreshComplete();
                if (i==9016){
                    ShowToas.showToast(getActivity(),"网络连接异常请检查你的网络设置");
                }
            }
        });
    }

    /**
     * 退出此界面时注意：fragment任然非空,其成员也非空,activity亦如此.在对象和其成员在内存里并没有释放
     * 故：1,应该将fragment的成员属性至null(空)，每次回到次界面view定义为static也就无意义了[static目的是实现在vp里的单列模式]
     *    2,设计为非单列模式，切view对象为非static，即可实现每次重绘界面ui
     *   【找不到释放当前类的方法，如有直接在退出此界面时释放该类所占的内存，即可解决数据显示的bug】
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.currentUser=null;
        this.invitationsList = null;
        this.bmobQuery = null;
        this.currentClickListItem = 0;
        this.view=null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==777 && resultCode==999) {
            int result_count = data.getIntExtra("result_count", 1);
            invitationsList.get(currentClickListItem).setDianzangCount(result_count);
        }
    }

    private int currentClickListItem;//当前被点击的list集合的position

    //delete item
    private void deleteItem(final String invitationId, final int listPositon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定删除该条帖子吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Invitation invitation = new Invitation();
                invitation.delete(getActivity(), invitationId, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        //刷新列表
                        invitationsList.remove(listPositon);//根据角标移除集合里的此item
                        fragment1PulltoRefrehLiatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            }
        }).setNegativeButton("取消", null).create().show();

    }
}

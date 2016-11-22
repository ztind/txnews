package com.zt.txnews.fragment;

import android.app.Activity;
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
import com.zt.txnews.activity.FriendsActivity;
import com.zt.txnews.activity.InvitationActivity;
import com.zt.txnews.adapter.Fragment1PulltoRefrehLiatAdapter;
import com.zt.txnews.bean.Invitation;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/9/19.
 */
public class FriendsvpFragment extends Fragment {

    private View view;
    private PullToRefreshListView pullToRefreshListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view==null) {
            view = inflater.inflate(R.layout.activity_friends_vp_listview, container, false);
            initView();
            initViewData();
            return view;
        }
        return view;
    }
    //fragment 加载进入activity时回掉,见帖子对象传递过来
    private Invitation invitation_f;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        FriendsActivity friendsActivity= (FriendsActivity)activity;
        friendsActivity.setInterfaceInvitationListener(new FriendsActivity.interfaceInvitationListener() {
            @Override
            public void setInterfaceInvitationListener(Invitation invitation) {
                if (invitation != null) {
                    invitation_f = invitation;
                }
            }
        });
    }

    private void initView() {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.friends_vi_pulltorefreshlistview);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initViewData();
            }
        });
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
                Invitation invitation = (Invitation) adapter.getItem(position - 1);
                Intent intent = new Intent(getActivity(), InvitationActivity.class);
                intent.putExtra("invitation", invitation);
                startActivityForResult(intent, 666);
            }
        });

    }
    private int limit=10;
    private List<Invitation> invitationList;
    private Fragment1PulltoRefrehLiatAdapter adapter;
    private void initViewData() {
        if (invitation_f!=null) {
            String userId = invitation_f.getUserId();
            BmobQuery<Invitation> bmobQuery = new BmobQuery<>();

            bmobQuery.setLimit(limit);
            bmobQuery.setSkip(invitationList==null?0:invitationList.size());
            bmobQuery.order("-createdAt");
            bmobQuery.addWhereEqualTo("userId", userId);

            //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
            boolean isCache = bmobQuery.hasCachedResult(getActivity(), Invitation.class);
            if(isCache){
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
            }else{
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            }

            bmobQuery.findObjects(getActivity(), new FindListener<Invitation>() {
                @Override
                public void onSuccess(List<Invitation> list) {
                    if (invitationList==null){
                        if (list!=null) {
                            invitationList = list;
                            adapter = new Fragment1PulltoRefrehLiatAdapter(getActivity(), list);
                            pullToRefreshListView.setAdapter(adapter);
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }else{
                        for (Invitation invitation:list) {
                            invitationList.add(invitation);
                        }
                        pullToRefreshListView.onRefreshComplete();
                    }
                }
                @Override
                public void onError(int i, String s) {
                    pullToRefreshListView.onRefreshComplete();
                }
            });
        }
    }

    private int currentClickListItem;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==666 && resultCode==999) {
            int result_count = data.getIntExtra("result_count", 1);
            invitationList.get(currentClickListItem).setDianzangCount(result_count);
        }
    }
}

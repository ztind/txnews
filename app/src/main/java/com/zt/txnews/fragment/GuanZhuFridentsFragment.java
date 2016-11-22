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
import com.zt.txnews.activity.FriendsActivity;
import com.zt.txnews.adapter.Fragment2ListviewAdapter;
import com.zt.txnews.bean.Friends;
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
 *
 */
public class GuanZhuFridentsFragment extends Fragment {
    private PullToRefreshListView pullToRefreshListView;
    private  View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_2, container, false);
        initView();
        initData();
        return view;
    }
    private void initView() {
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.fragment2_pulltorefreshlistview);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                initData();
            }
        });
        //设置上啦提示文本
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);//写在设置之前

        ILoadingLayout proxy2 = pullToRefreshListView.getLoadingLayoutProxy(false,true);
        proxy2.setPullLabel("上拉加载...");
        proxy2.setRefreshingLabel("正在玩命加载中...");
        proxy2.setReleaseLabel("放开加载...");

        //设置item的点击事件，跳转到好友主页
        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter!=null) {
                    Friends friends = (Friends)adapter.getItem(position - 1);
                    String f_id = friends.getFriendsId();
                    BmobQuery<Invitation> bmobQuery = new BmobQuery<>();
                    bmobQuery.setLimit(1);
                    bmobQuery.addWhereEqualTo("userId", f_id);

                    bmobQuery.findObjects(getActivity(), new FindListener<Invitation>() {
                        @Override
                        public void onSuccess(List<Invitation> list) {
                            if (list!=null && list.size()>0){
                                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                                intent.putExtra("invitationfriend", list.get(0));
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onError(int i, String s) {
                            ShowToas.showToast(getActivity(),"网络连接异常，请连接后重试");
                        }
                    });
                }
            }
        });
        //长按取消关注
        pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Friends friends = (Friends) adapter.getItem(position - 1);
                cancelHuanzhu(friends.getObjectId(),position-1);
                return false;
            }
        });
    }

    private int limit = 10;
    private List<Friends> friendsList;
    private Fragment2ListviewAdapter adapter;
    private void initData() {
        User currentUser = BmobUser.getCurrentUser(getActivity(), User.class);
        if (currentUser==null){
                return;
        }
        BmobQuery<Friends> bmobQuery = new BmobQuery<>();
        bmobQuery.setLimit(limit);
        bmobQuery.setSkip(friendsList == null ? 0 : friendsList.size());
        bmobQuery.order("-createdAt");
        bmobQuery.addWhereEqualTo("meId",currentUser.getObjectId());

        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = bmobQuery.hasCachedResult(getActivity(), Friends.class);
        if (isCache) {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }

        bmobQuery.findObjects(getActivity(), new FindListener<Friends>() {
            @Override
            public void onSuccess(List<Friends> list) {
                if (friendsList==null){
                    if (list!=null && list.size()>0) {
                        friendsList = list;
                        //设置一次adapter
                        adapter = new Fragment2ListviewAdapter(getActivity(),list);
                        pullToRefreshListView.setAdapter(adapter);
                        pullToRefreshListView.onRefreshComplete();
                    }
                }else{
                    if (list!=null && list.size()>0){
                        for (Friends friends:list) {
                            friendsList.add(friends);
                        }
                    }
                    pullToRefreshListView.onRefreshComplete();
                }
            }
            @Override
            public void onError(int i, String s) {

            }
        });
    }
   //取消关注[从friends表里删除即可]
    private void  cancelHuanzhu(final String id, final int listPosition) {
        User currentUser = BmobUser.getCurrentUser(getActivity(), User.class);
        if (currentUser==null) {
            ShowToas.showToast(getActivity(), "请登录后操作");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("取消关注该贴友");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Friends friends = new Friends();
                friends.delete(getActivity(), id, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        //刷新列表
                        friendsList.remove(listPosition);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(int i, String s) {

                    }
                });

            }
        }).setNegativeButton("取消", null).create().show();

    }
}

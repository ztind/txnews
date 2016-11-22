package com.zt.txnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.bean.Friends;
import com.zt.txnews.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by Administrator on 2016/9/19.
 * 我的个人中心的Fragment的pulltorefreshlistview的适配器
 */
public class Fragment2ListviewAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Friends> list;
    private Picasso picasso;
    private Context context;
    public Fragment2ListviewAdapter(Context context,List<Friends> list) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.picasso = Picasso.with(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null) {
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.fragment2_listview_item,parent,false);
            vh.photo = (CircularImageView) convertView.findViewById(R.id.fragment2_photo);
            vh.nameText = (TextView) convertView.findViewById(R.id.fragment2_name);
            vh.mottoText = (TextView) convertView.findViewById(R.id.fragment2_motto);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        //赋值
        Friends friend = list.get(position);
        picasso.load(friend.getFriendPhotoIcon()).into(vh.photo);
        vh.nameText.setText(friend.getFriendsName());
        //获取好友座右铭
        String id = friend.getFriendsId();
        getMotto(id,vh.mottoText);
        return convertView;
    }
    class ViewHolder{
        CircularImageView photo;
        TextView nameText,mottoText;
    }
    private void getMotto(String id, final TextView mottoText){
        BmobQuery<User> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(context, id, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                if (user!=null){
                    mottoText.setText(user.getMotto());
                }
            }
            @Override
            public void onFailure(int i, String s) {

            }
        });
    }
}

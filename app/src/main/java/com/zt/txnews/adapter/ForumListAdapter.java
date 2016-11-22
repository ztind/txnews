package com.zt.txnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.bean.Invitation;

import java.util.List;

/**
 * Created by Administrator on 2016/9/15.
 * 帖子列表适配器
 */
public class ForumListAdapter extends BaseAdapter{
    private Picasso picasso;
    private LayoutInflater layoutInflater;
    private List<Invitation> list;
    public ForumListAdapter(Context context,List<Invitation> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.picasso = Picasso.with(context);
        this.list = list;
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
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.invitation_list_item, parent, false);
            vh.photo = (ImageView) convertView.findViewById(R.id.forum_listview_item_photo);
            vh.name = (TextView) convertView.findViewById(R.id.forum_listview_item_name);
            vh.title = (TextView) convertView.findViewById(R.id.forum_listview_item_title);
            vh.content = (TextView) convertView.findViewById(R.id.forum_listview_item_content);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Invitation invitation = list.get(position);
        picasso.load(invitation.getIconUrl()).into(vh.photo);
        vh.name.setText(invitation.getName());
        vh.title.setText(invitation.getTitle());
        vh.content.setText("    "+invitation.getContent());

        return convertView;
    }
    class ViewHolder{
        private ImageView photo;
        private TextView name;
        private TextView title;
        private TextView content;
    }
}

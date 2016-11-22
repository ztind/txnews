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
import com.zt.txnews.bean.News;

import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 */
public class ListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<News> list;
    private Picasso picasso;
    public ListViewAdapter(Context context,List<News> list) {
        this.mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.listview_item, parent, false);
            vh.imageView = (ImageView) convertView.findViewById(R.id.listview_item_image);
            vh.title = (TextView) convertView.findViewById(R.id.listview_item_title);
            vh.updateTime = (TextView) convertView.findViewById(R.id.listview_item_updatetime);
            vh.authorName = (TextView) convertView.findViewById(R.id.listview_item_authorname);
            //设置tag
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        //赋值
        News news = list.get(position);
        if(news.getPicUrl()!=null && news.getPicUrl().length()>0) {
            picasso.load(news.getPicUrl()).into(vh.imageView);
        }
        vh.title.setText(news.getTitle());
        vh.updateTime.setText(news.getUpdateTime());
        vh.authorName.setText(news.getAuthorName());

        return convertView;
    }
    class ViewHolder{
        TextView title,updateTime,authorName;
        ImageView imageView;
    }
}

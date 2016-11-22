package com.zt.txnews.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zt.txnews.R;
import com.zt.txnews.activity.FlowLayout;
import com.zt.txnews.bean.Comment;

import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/9/16.
 * 帖子评论列表适配器[点赞应该注意3bug的解决：1.每个条评论在此次当前只能点赞一次 2.点赞数的显示bug 3.点赞图标显示的bug]
 */
public class CommentAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Comment> list;
    private Picasso picasso;
    private Context context;
    public  CommentAdapter(Context context,List<Comment> list) {
        this.layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.picasso = Picasso.with(context);
        Log.v("TAG", list.size() + "");
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView==null) {
            vh = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.comment_list_item, parent, false);
            vh.photoImage = (ImageView) convertView.findViewById(R.id.comment_list_photo);
            vh.nameText = (TextView) convertView.findViewById(R.id.comment_liat_name);
            vh.contentText = (TextView) convertView.findViewById(R.id.comment_list_content);
            vh.zangImage = (ImageView) convertView.findViewById(R.id.comment_list_zangimage);
            vh.zangCountText = (TextView) convertView.findViewById(R.id.comment_list_zangcount);
//            vh.commentImage = (ImageView) convertView.findViewById(R.id.comment_list_commmentimage);
//            vh.flowLayout = (FlowLayout) convertView.findViewById(R.id.comment_list_flowlayout);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }
        final Comment commentBean = list.get(position);
        picasso.load(commentBean.getIconUrl()).into(vh.photoImage);
        vh.nameText.setText(commentBean.getName());
        vh.contentText.setText("  " + commentBean.getCommentContent());

        //注意点赞数和点赞图标的在点击后滑动显示的bug
        vh.zangCountText.setText(commentBean.getDianzangCount() + "");

        if (commentBean.getIsPressZhan()==0){  //未点赞
            vh.zangImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.zhan_no_press));
        }else if (commentBean.getIsPressZhan()==1){ //已点赞
            vh.zangImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.zhan_press));
        }
        //确保实列化一次接口即可
        if(commentBean.getIsPressZhan()==0){
            vh.zangImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (commentBean.getIsPressZhan()==1){
                        return;  //确保只能点一次赞
                    }
                    //update bmob comment zancount
                    Comment comment = new Comment();
                    comment.setDianzangCount(commentBean.getDianzangCount()+1);
                    comment.update(context, commentBean.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            vh.zangImage.setImageDrawable(context.getResources().getDrawable(R.mipmap.zhan_press)); //图标变为点中状态
                            vh.zangCountText.setText((commentBean.getDianzangCount() + 1) + "");  //点赞数在textview显示+1
                            //1,通过改变commentBean的点赞数来解决滑动时点赞数显示个数出错的bug**********************************************
                            commentBean.setDianzangCount(commentBean.getDianzangCount()+1);
                            //2，设置该条评论在此次当前页面已经被点赞了，设置状态值为1即可解决点赞图标显示的bug**********************
                            commentBean.setIsPressZhan(1);
                        }
                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            });
        }
        //对评论回复
//        vh.commentImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowToas.showToast(context, position + "");
//            }
//        });
        //移除所有view
//        vh.flowLayout.removeAllViews();


        return convertView;
    }
    class ViewHolder{
        ImageView photoImage,zangImage,commentImage;
        TextView nameText,zangCountText, contentText;
        FlowLayout flowLayout;
    }
}

package com.zt.txnews.newsdata;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zt.txnews.activity.NewsMessageActivity;
import com.zt.txnews.adapter.ListViewAdapter;
import com.zt.txnews.bean.News;
import com.zt.txnews.http.OkHttpManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 * 新闻数据获取类
 */
/**
 * 	类型,,top(头条，默认),shehui(社会),guonei(国内),guoji(国际),yule(娱乐),tiyu(体育)junshi(军事),keji(科技),caijing(财经),shishang(时尚)
 *
 * 	*****************************************************UI控件的统一数据填充模式******************************************************************************
 */
public class NewsDate {
    private String category;
    private Context context;

    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private Button updateBut;
    private PullToRefreshListView pullToRefreshListView;

    public NewsDate(final Context context,String category,ProgressBar progressBar,RelativeLayout relativeLayout,Button button, final PullToRefreshListView pullToRefreshListView) {
        this.context = context;
        this.category = category;
        this.progressBar = progressBar;
        this.relativeLayout = relativeLayout;
        this.updateBut = button;
        this.pullToRefreshListView = pullToRefreshListView;

        //设置下拉刷新数据（为接口发起方实例化接口对象， 只需一次即可）
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestJuheServiceGetData(); //从新获取该类型的数据
            }
        });
        //设置下啦提示文本
        ILoadingLayout proxy = pullToRefreshListView.getLoadingLayoutProxy(true,false);
        proxy.setRefreshingLabel("正在玩命加载中...");

    }

    //请求数据 请求示例：http://v.juhe.cn/toutiao/index?type=top&key=APPKEY
    public void requestJuheServiceGetData(){
        String url = "http://v.juhe.cn/toutiao/index?type="+category+"&key=aa86168aa79302580d8a91e57a0f9400";
        OkHttpManager.getInstance().asyncJsonStringByURL(url, new OkHttpManager.Func1() {
            @Override
            public void onResponse(String jsonString) {
                if(jsonString!=null){
                    ArrayList<News> list = parseData(jsonString);
                    showDataInUI(category,list);
                }
            }

            @Override
            public void onFailure(IOException exception) {
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);//显示网络链接
                updateBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestJuheServiceGetData(); //从新获取该类型的数据
                    }
                });

                if (pullToRefreshListView.isShown()){
                    pullToRefreshListView.onRefreshComplete();//关闭试图
                }
            }
        });
    }
    //解析json
    protected ArrayList<News> parseData(String json) {
        try {
            if(json==null){
                return null;
            }
            JSONObject jsonObject = new JSONObject(json);
            JSONObject rs = jsonObject.getJSONObject("result");
            JSONArray jsonArray = rs.getJSONArray("data");
            ArrayList<News> newsDataList = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                News news = new News();
                news.authorName = jsonObj.getString("author_name");
                news.updateTime = jsonObj.getString("date");
                news.title = jsonObj.getString("title");
                news.url = jsonObj.getString("url");
                if(jsonObj.has("thumbnail_pic_s")){
                    news.picUrl = jsonObj.getString("thumbnail_pic_s");
                }
                newsDataList.add(news);
            }
            return newsDataList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showDataInUI(String category, List<News> myList) {

            progressBar.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);

            final ListViewAdapter adapter=new ListViewAdapter(context,myList);
            pullToRefreshListView.setAdapter(adapter);

            //根据构造方法传入的pullToRefreshListView来设置item的点击事件
         /**
          *   pullToRefreshListView的item从1开始
          */
            pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News news = (News) adapter.getItem(position-1);
//                    Log.v("TAG", position+"---"+news.getAuthorName() + "/" + news.getTitle());
                    Intent intent = new Intent(context, NewsMessageActivity.class);
                    intent.putExtra("url", news.getUrl());
                    context.startActivity(intent);
                }
            });
        if (pullToRefreshListView.isShown()){
            pullToRefreshListView.onRefreshComplete();//关闭试图
        }
    }
}


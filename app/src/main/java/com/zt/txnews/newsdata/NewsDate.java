package com.zt.txnews.newsdata;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;
import com.zt.txnews.activity.NewsMessageActivity;
import com.zt.txnews.adapter.ListViewAdapter;
import com.zt.txnews.bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                getNewsByCategory(); //从新获取该类型的数据
            }
        });
        //设置下啦提示文本
        ILoadingLayout proxy = pullToRefreshListView.getLoadingLayoutProxy(true,false);
        proxy.setRefreshingLabel("正在玩命加载中...");

    }
    //新闻头条
    public  void getNewsByCategory() {
        Parameters ps = new Parameters();
        ps.add("type", category);
        JuheData.executeWithAPI(context, 235, "http://v.juhe.cn/toutiao/index", JuheData.GET, ps, new MyCallBack());//开启一个线程去service获取数据，so 下面的可能会先执行
    }

    class MyCallBack implements DataCallBack{

        @Override
        public void onSuccess(int i, String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String reason = jsonObject.getString("reason");
                JSONObject result = jsonObject.getJSONObject("result");
                JSONArray data = result.getJSONArray("data");
                List<News>  myList = new ArrayList<>();  //News封装集合
                News news=null;
                for (int k = 0; k < data.length(); k++) {
                    JSONObject item = data.getJSONObject(k);
                    String title = item.getString("title");
                    String updateTime = item.getString("date");
                    String author_name = item.getString("author_name");
                    String picUrl = item.getString("thumbnail_pic_s");
                    String url = item.getString("url");
//                    Log.v("TAG", k + "---" + item.toString() + "\n");
                    news = new News(title, updateTime, author_name, picUrl, url);
                    myList.add(news);
                    /**
                     * 在军事板块data.length()==40 循环到第32此时发现没有json对象就抛出异常so实则循环的了31次,so没有执行到showDataInUI(category, myList); so出现了显示不出军事页面的bug
                     */
                  if (category.equals("junshi") && myList.size()==31){
                        break;
                  }
                }
                //在拿到数据列表后直接去填从显示到UI控件上
                showDataInUI(category, myList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFinish() {}
        @Override
        public void onFailure(int i, String s, Throwable throwable) {
            Log.v("TAG", i + "--" + throwable.getMessage());
            if (i==30002){
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);//显示网络链接
                updateBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getNewsByCategory(); //从新获取该类型的数据
                    }
                });
                if (pullToRefreshListView.isShown()){
                    pullToRefreshListView.onRefreshComplete();//关闭试图
                }
            }
        }
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


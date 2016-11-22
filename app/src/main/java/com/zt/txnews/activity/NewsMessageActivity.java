package com.zt.txnews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.zt.jackone.AppConnect;
import com.zt.txnews.R;

/**
 * Created by Administrator on 2016/9/10.
 * 新闻详情页
 */
public class NewsMessageActivity extends Activity{
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsmessage_layout);
        initView();
    }

    private void initView() {
        LinearLayout adlayout =(LinearLayout)findViewById(R.id.AdLinearLayout);
        AppConnect.getInstance(this).showBannerAd(this, adlayout);

        webView = (WebView) findViewById(R.id.webView);
        String url = getIntent().getExtras().getString("url");
        webView.loadUrl(url);


    }
}

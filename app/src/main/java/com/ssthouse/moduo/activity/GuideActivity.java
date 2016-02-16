package com.ssthouse.moduo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ssthouse.moduo.R;

/**
 * 欢迎界面
 * Created by ssthouse on 2016/1/15.
 */
public class GuideActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fist_in);

        initView();
        loadHtml();
    }

    /**
     * 加载webView
     */
    private void initView() {
        webView = (WebView) findViewById(R.id.id_web_view);
    }

    private void loadHtml(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        webView.loadUrl("file:///android_asset/guide/index.html");
    }
}

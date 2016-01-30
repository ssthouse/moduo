package com.ssthouse.moduo.main.view.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ssthouse.moduo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 常见问题fragment
 * Created by ssthouse on 2016/1/26.
 */
public class CommonIssueFragment extends Fragment {

    @Bind(R.id.id_web_view)
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common_issue, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        ButterKnife.bind(this, rootView);

        //初始化webView
        webView.loadUrl("file:///android_asset/CommonIssue/index.html");
    }
}

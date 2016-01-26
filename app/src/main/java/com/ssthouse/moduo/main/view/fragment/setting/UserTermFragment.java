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
 * 使用条款fragment
 * Created by ssthouse on 2016/1/26.
 */
public class UserTermFragment extends Fragment {

    @Bind(R.id.id_web_view)
    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_term, container, false);
        ButterKnife.bind(this, rootView);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        //初始化webView

    }

}

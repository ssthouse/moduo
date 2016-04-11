package com.ssthouse.moduo.fragment.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;

/**
 * 消息中心的通知的详情页面Fragment
 * Created by ssthouse on 2016/1/21.
 */
public class MsgDetailFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_detail, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
    }

    /**
     * todo
     * 传入新的数据---刷新UI
     * 刷新UI
     */
    private void updateUI(){

    }
}

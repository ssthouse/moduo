package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;

import timber.log.Timber;

/**
 * 用户账号
 * Created by ssthouse on 2016/1/13.
 */
public class UserInfoFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        initView(rootView);
        Timber.e("onCreateView");
        return rootView;
    }

    private void initView(View rootView) {


    }
}

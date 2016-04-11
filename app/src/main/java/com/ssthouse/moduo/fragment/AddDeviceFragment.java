package com.ssthouse.moduo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;

/**
 * 添加家电连接fragment(暂时无用)
 * Created by ssthouse on 2016/1/14.
 */
public class AddDeviceFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_device, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {


    }
}

package com.ssthouse.moduo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;

/**
 * 电器列表fragment
 * Created by ssthouse on 2016/1/14.
 */
public class DeviceListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {

    }
}

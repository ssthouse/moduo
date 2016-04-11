package com.ssthouse.moduo.fragment.gesture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;

import butterknife.ButterKnife;

/**
 * 显示当前正在加载, 提示用户等待的Fragment
 * Created by ssthouse on 2016/3/13.
 */
public class EmptyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_empty, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initView() {

    }

}

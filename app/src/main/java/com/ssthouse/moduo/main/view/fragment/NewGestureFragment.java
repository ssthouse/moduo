package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.gesture.LockPatternView;
import com.ssthouse.moduo.R;

/**
 * 新建手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class NewGestureFragment extends Fragment{

    private LockPatternView lockView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_gesture, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        lockView = (LockPatternView) rootView.findViewById(R.id.id_gesture_lock);
    }
}

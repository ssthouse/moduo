package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ssthouse.moduo.R;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment {


    private RelativeLayout surfaceViewLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView  = inflater.inflate(R.layout.fragment_video_display, null);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {


    }
}

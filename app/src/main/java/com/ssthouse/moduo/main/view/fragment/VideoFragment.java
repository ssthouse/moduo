package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.video.VideoHolder;
import com.ssthouse.moduo.main.view.activity.VideoActivity;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment {

    private MaterialDialog waitDialog;

    /**
     * 视频承接控件
     */
    private RelativeLayout surfaceViewLayout;

    private VideoHolder videoHolder;
    private long streamerCid;// 要观看的采集端cid

    /**
     * 获取fragment实例
     *
     * @param cidNumber
     * @return
     */
    public static VideoFragment newInstance(long cidNumber) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(VideoActivity.ARGUMENT_CID_NUMBER, cidNumber);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化数据
        streamerCid = getArguments().getLong(VideoActivity.ARGUMENT_CID_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //初始化view
        View rootView = inflater.inflate(R.layout.fragment_video_display, container, false);
        surfaceViewLayout = (RelativeLayout) rootView.findViewById(R.id.id_rl_container);

        //初始化视频播放类
        videoHolder = new VideoHolder(getContext(), surfaceViewLayout, streamerCid);

        return rootView;
    }

    /*
    生命周期控制
     */
    @Override
    public void onStart() {
        super.onStart();
        videoHolder.startLiveVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoHolder.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        videoHolder.stop();
    }
}

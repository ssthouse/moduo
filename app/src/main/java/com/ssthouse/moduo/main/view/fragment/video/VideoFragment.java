package com.ssthouse.moduo.main.view.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.gyroscope.GyroscopeSensor;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.video.VideoHolder;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.VideoActivity;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment {

    private MaterialDialog waitDialog;

    /**
     * 陀螺仪管理类
     */
    private GyroscopeSensor gyroscopeSensor;

    /**
     * 视频逻辑管理类
     */
    private VideoHolder videoHolder;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //初始化view
        View rootView = inflater.inflate(R.layout.fragment_video_display, container, false);
        initView(rootView);

        //初始化视频播放类
        videoHolder = new VideoHolder(getContext(),
                (RelativeLayout) rootView.findViewById(R.id.id_rl_container),
                getArguments().getLong(VideoActivity.ARGUMENT_CID_NUMBER));

        //初始化传感器
        gyroscopeSensor = new GyroscopeSensor(getContext());
        gyroscopeSensor.setListener(new GyroscopeSensor.RotationChangeListener() {
            @Override
            public void call(int deltaX, int deltaY, int deltaZ) {
                //// TODO: 2016/1/15 发送方向操作
                XPGController.getInstance(getContext()).getmCenter().cWriteHead(
                        XPGController.getCurrentDevice().getXpgWifiDevice(),
                        deltaX,
                        deltaY,
                        deltaZ
                );
            }
        });

        return rootView;
    }

    private void initView(View rootView) {
        Switch sw = (Switch) rootView.findViewById(R.id.id_sw_sensor_control);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gyroscopeSensor.resetOrientation();
                    gyroscopeSensor.start();
                } else {
                    gyroscopeSensor.pause();
                }
            }
        });
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
        gyroscopeSensor.pause();
    }
}

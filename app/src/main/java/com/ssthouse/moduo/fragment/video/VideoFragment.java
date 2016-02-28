package com.ssthouse.moduo.fragment.video;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ssthouse.gyroscope.GyroscopeSensor;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.video.VideoActivity;
import com.ssthouse.moduo.control.video.VideoHolder;
import com.ssthouse.moduo.control.xpg.XPGController;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment implements VideoFragmentView {

    //Presenter
    private VideoFragmentPresenter mPresenter;

    //控制面板是否可见
    private boolean isCtrlPanelVisible;

    private AlertDialog waitDialog;
    private View waitDialogView;
    private AlertDialog confirmDialog;
    private View confirmDialogView;


    //视频承接view
    @Bind(R.id.id_rl_container)
    View videoContainer;

    //***********控制面板**************************
    @Bind(R.id.id_ll_control)
    LinearLayout llControlPanel;

    //陀螺仪控制开关
    @Bind(R.id.id_sw_sensor_control)
    Switch swGyroscopeControl;

    //挂断
    @Bind(R.id.id_tv_hangup)
    TextView tvHangup;

    //对讲开关
    @Bind(R.id.id_sw_toggle_voice)
    Switch swToggleVoice;

    //全屏切换
    @Bind(R.id.id_iv_full_screen)
    ImageView ivFullScreen;

    /**
     * 陀螺仪管理类
     */
    private GyroscopeSensor gyroscopeSensor;

    /**
     * 视频逻辑管理类
     */
    private VideoHolder videoHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_display, container, false);
        ButterKnife.bind(this, rootView);
        initView();

        //初始化视频参数
        initVideo();

        //Presenter
        mPresenter = new VideoFragmentPresenter(getContext(), this);
        return rootView;
    }

    private void initView() {
        //体感控制开关
        swGyroscopeControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        //挂断
        tvHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出Activity  会自动清除video数据点
                getActivity().finish();
            }
        });

        //对讲开关
        swToggleVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                XPGController.getInstance(getContext())
                        .getmCenter()
                        .cWriteCmd(XPGController.getCurrentDevice().getXpgWifiDevice(),
                                Byte.decode("1"),
                                Byte.decode("1"),
                                Byte.decode("1"),
                                Byte.decode("1"));
                if (isChecked) {
                    videoHolder.startTalk();
                } else {
                    videoHolder.stopTalk();
                }
            }
        });

        //全屏控制
        ivFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VideoActivity.isPortrait) {
                    //横屏
                    toLandscape();
                } else {
                    //编变竖屏
                    toPortrait();
                }
            }
        });

        //控制面板Visible控制
        videoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只有横屏会隐藏控制面板
                if (isCtrlPanelVisible && VideoActivity.isPortrait == false) {
                    hideCtrlPanel();
                } else {
                    showCtrlPanel();
                }
            }
        });

        LayoutInflater inflater = LayoutInflater.from(getContext());
        waitDialogView = inflater.inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .setCancelable(false)
                .create();
        //初始等待Dialog
        showWaitDialog("正在加载视频");

        confirmDialogView = inflater.inflate(R.layout.dialog_confirm, null);
        confirmDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .setCancelable(false)
                .create();
    }

    //初始化video
    private void initVideo() {
        //启动采集端的视频
        //将video数据点置为1
        XPGController.getInstance(getContext()).getmCenter().cWriteVideo(
                XPGController.getCurrentDevice().getXpgWifiDevice(), 1
        );

        //初始化视频播放类
        videoHolder = new VideoHolder(getContext(),
                (RelativeLayout) videoContainer,
                Long.parseLong(XPGController.getCurrentDevice().getVideoCidNumber()));

        //初始化传感器
        gyroscopeSensor = new GyroscopeSensor(getContext());
        gyroscopeSensor.setListener(new GyroscopeSensor.RotationChangeListener() {
            @Override
            public void call(int deltaX, int deltaY, int deltaZ) {
                Timber.e("%d              %d                   %d", -deltaX, deltaY, -deltaZ);
                XPGController.getInstance(getContext()).getmCenter()
                        .cWriteHead(XPGController.getCurrentDevice().getXpgWifiDevice(),
                                -deltaX,
                                deltaY,
                                -deltaZ
                        );
            }
        });
    }

    @Override
    public void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissWaitDialog() {
        waitDialog.dismiss();
    }

    //显示确认退出Dialog
    @Override
    public void showConfirmDialog(String msg) {
        TextView tvContent = (TextView) confirmDialogView.findViewById(R.id.id_tv_content);
        tvContent.setText(msg);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        };
        confirmDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(clickListener);
        confirmDialogView.findViewById(R.id.id_iv_close).setOnClickListener(clickListener);
        confirmDialog.show();
    }

    @Override
    public void hideCtrlPanel() {
        isCtrlPanelVisible = false;
        //下方面板
        if (llControlPanel != null) {
            llControlPanel.setVisibility(View.GONE);
        }
        //actionbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().hide();
    }

    @Override
    public void showCtrlPanel() {
        isCtrlPanelVisible = true;
        //下方面板
        if (llControlPanel != null) {
            llControlPanel.setVisibility(View.VISIBLE);
        }
        //actionbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().show();
    }

    //横屏
    @Override
    public void toLandscape() {
        Timber.e("横屏");
        VideoActivity.isPortrait = false;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //隐藏控制面板
        llControlPanel.setVisibility(View.GONE);
        //全屏---隐藏actionbar
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.findViewById(R.id.id_tb).setVisibility(View.GONE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    //竖屏
    @Override
    public void toPortrait() {
        Timber.e("竖屏");
        VideoActivity.isPortrait = true;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //非全屏---显示actionbar
        AppCompatActivity videoActivity = (AppCompatActivity) getActivity();
        videoActivity.getSupportActionBar().show();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}

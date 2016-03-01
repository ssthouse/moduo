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

import timber.log.Timber;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment implements VideoFragmentView {

    //Presenter
    private VideoFragmentPresenter mPresenter;

    //Dialog
    private AlertDialog waitDialog;
    private View waitDialogView;
    private AlertDialog confirmDialog;
    private View confirmDialogView;

    //陀螺仪管理类
    private GyroscopeSensor gyroscopeSensor;
    private boolean isInSensorControl = false;

    //视频逻辑管理类
    private VideoHolder videoHolder;

    //**********竖屏控件**************************
    //视频承接
    private RelativeLayout rlVideoContainerPort;
    //控制面板
    //控制面板是否正常状态---默认是
    private boolean isPanelNormal = true;
    private Switch swSensorControlPort;
    private Switch swToggleVoicePort;
    private TextView tvHangupPort;
    //一排操作按钮
    private ImageView ivRestorePanelPort;
    private ImageView ivSensorControlPort;
    private ImageView ivTakePhotoPort;
    //录像---是否在录像标志位
    private ImageView ivTakeVideoPort;
    private boolean isInVideoRecord;
    //静音按钮
    private ImageView ivMutePort;
    private boolean isMute = false;
    private ImageView ivVolumeDownPort;
    private ImageView ivVolumeUpPort;
    private ImageView ivFullScreenPort;


    //********横屏控件****************************
    //视频承接
    private RelativeLayout rlVideoContainerLand;
    //控制面板
    private LinearLayout llTopControlLand;
    private ImageView ivBackLand;
    private LinearLayout llBottomControlLand;
    private Switch swSensorControlPortLand;
    private Switch swToggleVoicePortLand;
    private TextView tvHangupPortLand;
    private ImageView ivFullScreenPortLand;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView;
        if (VideoActivity.isPortrait) {
            rootView = inflater.inflate(R.layout.fragment_video_portrait, container, false);
            initPortView(rootView);
            //初始化视频参数
            initVideo(true);
        } else {
            rootView = inflater.inflate(R.layout.fragment_video_landscape, container, false);
            initLandView(rootView);
            //初始化视频参数
            initVideo(false);
        }

        initDialog();

        //Presenter
        mPresenter = new VideoFragmentPresenter(getContext(), this);
        return rootView;
    }

    //初始化横屏界面
    private void initLandView(View rootView) {
        //actionbar
        getActivity().findViewById(R.id.id_tb).setVisibility(View.GONE);

        //视频承接
        rlVideoContainerLand = (RelativeLayout) rootView.findViewById(R.id.id_rl_container);
        rlVideoContainerLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo---隐藏和显示控制面板---后期加动画
//                if (llTopControlLand.getVisibility() == View.VISIBLE) {
//                    llTopControlLand.setVisibility(View.GONE);
//                } else {
//                    llTopControlLand.setVisibility(View.VISIBLE);
//                }
                if (llBottomControlLand.getVisibility() == View.VISIBLE) {
                    llBottomControlLand.setVisibility(View.GONE);
                } else {
                    llBottomControlLand.setVisibility(View.VISIBLE);
                }
            }
        });

        //上方控制栏
        llTopControlLand = (LinearLayout) rootView.findViewById(R.id.id_ll_top_video_control);

        //返回竖屏
        ivBackLand = (ImageView) rootView.findViewById(R.id.id_iv_back_2_portrait);
        ivBackLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPortrait();
            }
        });

        //下方控制栏
        llBottomControlLand = (LinearLayout) rootView.findViewById(R.id.id_ll_bottom_video_control);

        swSensorControlPortLand = (Switch) rootView.findViewById(R.id.id_sw_sensor_control);
        swSensorControlPortLand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        swToggleVoicePortLand = (Switch) rootView.findViewById(R.id.id_sw_sensor_control);
        swToggleVoicePortLand.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        ivFullScreenPortLand = (ImageView) rootView.findViewById(R.id.id_iv_full_screen);
        ivFullScreenPortLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPortrait();
            }
        });

        //todo---一开始先隐藏控制栏
        llTopControlLand.setVisibility(View.GONE);
        llBottomControlLand.setVisibility(View.GONE);
    }

    //初始化竖屏界面
    private void initPortView(View rootView) {
        //actionbar
        getActivity().findViewById(R.id.id_tb).setVisibility(View.VISIBLE);

        //控制面板Visible控制--竖屏的时候没有监听事件
        rlVideoContainerPort = (RelativeLayout) rootView.findViewById(R.id.id_rl_container);

        //体感控制开关
        swSensorControlPort = (Switch) rootView.findViewById(R.id.id_sw_sensor_control);
        swSensorControlPort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        tvHangupPort = (TextView) rootView.findViewById(R.id.id_tv_hangup);
        tvHangupPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出Activity  会自动清除video数据点
                getActivity().finish();
            }
        });

        //对讲开关
        swToggleVoicePort = (Switch) rootView.findViewById(R.id.id_sw_toggle_voice);
        swToggleVoicePort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        ivFullScreenPort = (ImageView) rootView.findViewById(R.id.id_iv_full_screen);
        ivFullScreenPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //横屏
                toLandscape();
            }
        });

        //控制面板---从左往右
        ivRestorePanelPort = (ImageView) rootView.findViewById(R.id.id_iv_restore_control_panel);
        ivSensorControlPort = (ImageView) rootView.findViewById(R.id.id_iv_sensor_control);
        ivTakePhotoPort = (ImageView) rootView.findViewById(R.id.id_iv_take_photo);
        ivTakeVideoPort = (ImageView) rootView.findViewById(R.id.id_iv_take_video);
        ivMutePort = (ImageView) rootView.findViewById(R.id.id_iv_mute);
        ivVolumeDownPort = (ImageView) rootView.findViewById(R.id.id_iv_volume_down);
        ivVolumeUpPort = (ImageView) rootView.findViewById(R.id.id_iv_volume_up);

        //复原控制Panel
        ivRestorePanelPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreControlPanel();
            }
        });

        //体感控制
        ivSensorControlPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //状态变化
                isInSensorControl = !isInSensorControl;
                if (isInSensorControl) {
                    gyroscopeSensor.resetOrientation();
                    gyroscopeSensor.start();
                } else {
                    gyroscopeSensor.pause();
                }
            }
        });

        //拍照
        ivTakePhotoPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoHolder.saveOneFrameJpeg();
            }
        });

        //录像
        ivTakeVideoPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInVideoRecord = !isInVideoRecord;
                if (isInVideoRecord) {
                    mPresenter.startTakeVideo(videoHolder.getLiveStreamDid());
                } else {
                    mPresenter.stopTakeVideo(videoHolder.getLiveStreamDid());
                }
            }
        });

        //声音控制
        View.OnClickListener volumeControlListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.id_iv_mute:
                        isMute = !isMute;
                        if (isMute) {
                            ivMutePort.setImageResource(R.drawable.video_must);
                        } else {
                            ivMutePort.setImageResource(R.drawable.modo);
                        }
                        mPresenter.turnMute(isMute);
                        break;
                    case R.id.id_iv_volume_down:
                        mPresenter.turnDownVolume();
                        break;
                    case R.id.id_iv_volume_up:
                        mPresenter.turnUpVolume();
                        break;
                }
            }
        };
        ivMutePort.setOnClickListener(volumeControlListener);
        ivVolumeDownPort.setOnClickListener(volumeControlListener);
        ivVolumeUpPort.setOnClickListener(volumeControlListener);
    }

    //todo---恢复控制面板
    private void restoreControlPanel() {
        //隐藏三角
        ivRestorePanelPort.setVisibility(View.GONE);
        //其它的控件复原
        ivSensorControlPort.setVisibility(View.VISIBLE);
        ivTakePhotoPort.setVisibility(View.VISIBLE);
        ivTakeVideoPort.setVisibility(View.VISIBLE);
        ivMutePort.setVisibility(View.VISIBLE);
        ivVolumeDownPort.setVisibility(View.VISIBLE);
        ivVolumeUpPort.setVisibility(View.VISIBLE);
    }

    //放大拍照或者
    private void zoomButton(ImageView iv) {
        //显示三角形
        ivRestorePanelPort.setVisibility(View.VISIBLE);
        //隐藏所有panel按钮
        ivSensorControlPort.setVisibility(View.GONE);
        ivTakePhotoPort.setVisibility(View.GONE);
        ivTakeVideoPort.setVisibility(View.GONE);
        ivMutePort.setVisibility(View.GONE);
        ivVolumeDownPort.setVisibility(View.GONE);
        ivVolumeUpPort.setVisibility(View.GONE);
        //显示放大的按钮
        iv.setVisibility(View.VISIBLE);
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        waitDialogView = inflater.inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);
        //初始等待Dialog
        showWaitDialog("正在加载视频");

        confirmDialogView = inflater.inflate(R.layout.dialog_confirm, null);
        confirmDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .create();
        confirmDialog.setCanceledOnTouchOutside(false);
    }

    //初始化video---判断是否为竖屏
    private void initVideo(boolean isPort) {
        //启动采集端的视频
        //将video数据点置为1
        XPGController.getInstance(getContext()).getmCenter().cWriteVideo(
                XPGController.getCurrentDevice().getXpgWifiDevice(), 1
        );

        //初始化视频播放类
        if (isPort) {
            videoHolder = new VideoHolder(getContext(),
                    rlVideoContainerPort,
                    Long.parseLong(XPGController.getCurrentDevice().getVideoCidNumber()));
        } else {
            videoHolder = new VideoHolder(getContext(),
                    rlVideoContainerLand,
                    Long.parseLong(XPGController.getCurrentDevice().getVideoCidNumber()));
        }

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

    //横屏
    @Override
    public void toLandscape() {
        Timber.e("横屏");
        VideoActivity.isPortrait = false;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //隐藏控制面板
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

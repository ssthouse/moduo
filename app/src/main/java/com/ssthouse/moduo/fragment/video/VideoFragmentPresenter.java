package com.ssthouse.moduo.fragment.video;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

import com.ichano.rvs.viewer.Media;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.video.VideoExceptionEvent;
import com.ssthouse.moduo.model.event.video.VideoReadyEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceDataChangedEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Presenter
 * Created by ssthouse on 2016/2/15.
 */
public class VideoFragmentPresenter {

    private Context mContext;

    //View---Model
    private VideoFragmentView mVideoFragmentView;
    private VideoFragmentModel mVideoFragmentModel;

    private AudioManager mAudioManager;

    //当前录制的视频文件路径
    private String currentVideoPath;

    //构造方法
    public VideoFragmentPresenter(Context context, VideoFragmentView mVideoFragmentView) {
        this.mContext = context;
        this.mVideoFragmentView = mVideoFragmentView;
        mVideoFragmentModel = new VideoFragmentModel();
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        EventBus.getDefault().register(this);
    }

    //****************************音量控制*********************************
    public void turnUpVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_VIBRATE);
    }

    public void turnDownVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_VIBRATE);
    }

    public void turnMute(boolean isMute) {
        if (isMute) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }

    //********************拍照---视频控制**********************************
    //开始录像
    public void startTakeVideo(long streamerDid) {
        //获取media控制类
        Media media = Communication.getInstance(mContext).getmViewer().getMedia();
        //生成文件path
        currentVideoPath = FileUtil.generateNewVideoFilePath();
        media.startLocalRecord(streamerDid, currentVideoPath);
        Timber.e("start video");
    }

    //停止录像
    public void stopTakeVideo(long streamerDid) {
        //获取media控制类
        Media media = Communication.getInstance(mContext).getmViewer().getMedia();
        //停止录制
        media.stopLocalRecord(streamerDid);
        Toast.show("视频成功保存至:SD卡根目录\\" + currentVideoPath);
        Timber.e("stop video");
    }

    //拍照---接口貌似有问题---采取VideoHolder里面直接获取一帧数据流的方式截屏
//    public void takePhoto(long streamerCid, int streanerId) {
//        //获取media控制类
//        Media media = Communication.getInstance(mContext).getmViewer().getMedia();
//        //生成文件path
//        final String filePath = FileUtil.generateNewPicFilePath();
//        media.requestJpeg(streamerCid, 0, 0, RvsJpegType.HD, new RecvJpegListener() {
//            @Override
//            public void onRecvJpeg(long l, byte[] bytes) {
//                ToastHelper.show(mContext, "截图成功保存至:" + filePath);
//                Timber.e("数据长为:\t" + bytes.length);
//                try {
//                    OutputStream outputStream = new FileOutputStream(filePath);
//                    outputStream.write(bytes);
//                    outputStream.flush();
//                    outputStream.close();
//                    Timber.e("try complete pic");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Timber.e("wrong");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Timber.e("wrong");
//                }
//            }
//        });
//        Timber.e("did try pic in" + streamerCid);
//    }

    //设备数据变化的推送回调
    public void onEventMainThread(DeviceDataChangedEvent event) {
        if (event == null || event.getChangedDeviceData() == null) {
            Timber.e("设备推送的 变化的数据 为空");
            return;
        }
        if (!event.getChangedDeviceData().getVideo()) {
            mVideoFragmentView.showConfirmDialog("魔哆端退出视频通话");
        }
    }

    //视频数据加载完毕回调
    public void onEventMainThread(VideoReadyEvent event) {
        Timber.e("VideoReadyEvent");
        if (event.isSuccess()) {
            mVideoFragmentView.dismissWaitDialog();
        } else {
            mVideoFragmentView.dismissWaitDialog();
            //视频准备失败(点确定退出)
            mVideoFragmentView.showConfirmDialog("视频加载失败");
        }
    }

    //视频出错event
    public void onEventMainThread(VideoExceptionEvent event) {
        Timber.e("VideoExceptionEvent");
        mVideoFragmentView.showConfirmDialog("视频加载失败");
    }

    public void destroy() {
        //关闭视频数据点
        XPGController.getCurrentDevice().cWriteVideo(0);
        EventBus.getDefault().unregister(this);
    }
}

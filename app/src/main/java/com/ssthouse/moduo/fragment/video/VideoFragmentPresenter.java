package com.ssthouse.moduo.fragment.video;

import android.content.Context;

import com.ssthouse.moduo.control.xpg.XPGController;
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

    private VideoFragmentView mVideoFragmentView;
    private VideoFragmentModel mVideoFragmentModel;

    //构造方法
    public VideoFragmentPresenter(Context context, VideoFragmentView mVideoFragmentView) {
        this.mContext = context;
        this.mVideoFragmentView = mVideoFragmentView;
        mVideoFragmentModel = new VideoFragmentModel();
        EventBus.getDefault().register(this);
    }

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
        if (event.isSuccess()) {
            mVideoFragmentView.dismissWaitDialog();
        } else {
            mVideoFragmentView.dismissWaitDialog();
            //视频准备失败(点确定退出)
            mVideoFragmentView.showConfirmDialog("视频加载失败");
        }
    }

    public void destroy() {
        //关闭视频数据点
        XPGController.getInstance(mContext).getmCenter().cWriteVideo(
                XPGController.getCurrentDevice().getXpgWifiDevice(), 0
        );
        EventBus.getDefault().unregister(this);
    }
}

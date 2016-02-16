package com.ssthouse.moduo.fragment.video;

import com.ssthouse.moduo.model.bean.event.video.VideoReadyEvent;
import com.ssthouse.moduo.model.bean.event.xpg.DeviceDataChangedEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Presenter
 * Created by ssthouse on 2016/2/15.
 */
public class VideoFragmentPresenter {

    private VideoFragmentView mVideoFragmentView;
    private VideoFragmentModel mVideoFragmentModel;

    //构造方法
    public VideoFragmentPresenter(VideoFragmentView mVideoFragmentView) {
        this.mVideoFragmentView = mVideoFragmentView;
        mVideoFragmentModel = new VideoFragmentModel();
        EventBus.getDefault().register(this);
    }

    //设备数据变化的推送回调
    public void onEventMainThread(DeviceDataChangedEvent event){
        if(event == null || event.getChangedDeviceData() == null){
            Timber.e("设备推送的 变化的数据 为空");
            return;
        }
        if(!event.getChangedDeviceData().getVideo()){
            mVideoFragmentView.showConfirmDialog("魔哆端退出视频通话");
        }
    }

    //视频数据加载完毕回调
    public void onEventMainThread(VideoReadyEvent event){
        if(event.isSuccess()){
            mVideoFragmentView.dismissWaitDialog();
        }else{
            mVideoFragmentView.dismissWaitDialog();
            //视频准备失败(点确定退出)
            mVideoFragmentView.showConfirmDialog("视频加载失败");
        }
    }

    public void destroy(){
        EventBus.getDefault().unregister(this);
    }
}

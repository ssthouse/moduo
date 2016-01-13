package com.ssthouse.moduo.main.presenter;

import android.content.Context;

import com.ssthouse.moduo.bean.event.video.CallingResponseEvent;
import com.ssthouse.moduo.bean.event.video.VideoReadyEvent;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.main.view.VideoView;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Created by ssthouse on 2016/1/12.
 */
public class VideoPresenterImpl implements VideoPresenter {

    private Context context;

    //view层
    private VideoView videoView;

    /**
     * 构造方法
     *
     * @param context
     * @param videoView
     */
    public VideoPresenterImpl(Context context, VideoView videoView) {
        this.context = context;
        this.videoView = videoView;
        //初始化
        EventBus.getDefault().register(this);
    }

    /**
     * 视频加载完成回调
     *
     * @param event
     */
    public void onEventMainThread(VideoReadyEvent event) {
        videoView.dismissDialog();
    }

    /**
     * calling结果回调
     * @param event
     */
    public void onEventMainThread(CallingResponseEvent event){
        if(event.isSuccess()){
            videoView.showVideoFragment();
        }else{
            //// TODO: 2016/1/13 结束activity
            ToastHelper.show(context, "对方暂时无法接听");
        }
    }

    @Override
    public void startCalling() {
        Timber.e("开始打电话.....");
        //改变界面
        videoView.showCallingFragment();
        //// TODO: 2016/1/13 发出calling请求
    }

    /**
     * 等待视频加载
     */
    @Override
    public void waitForVideoReady() {
        videoView.showDialog("正在加载视频");
    }

    @Override
    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}

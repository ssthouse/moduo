package com.ssthouse.moduo.main.presenter;

import android.content.Context;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ssthouse.moduo.main.view.VideoView;

import de.greenrobot.event.EventBus;

/**
 *
 * Created by ssthouse on 2016/1/12.
 */
public class VideoPresenterImpl implements VideoPresenter{

    private Context context;

    //view层
    private VideoView videoView;

    /**
     * SDK控制类
     */
    private Viewer viewer;
    private Media media;

    /**
     * 构造方法
     * @param context
     * @param videoView
     */
    public VideoPresenterImpl(Context context, VideoView videoView) {
        this.context = context;
        this.videoView = videoView;
        //初始化
        EventBus.getDefault().register(this);
    }

    @Override
    public void startCalling() {
        //改变界面
        videoView.showCallingFragment();
        //todo---calling()先用延时模拟下
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //进入Video展示fragment
        videoView.showVideoFragment();
    }

    @Override
    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}

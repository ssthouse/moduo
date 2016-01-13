package com.ssthouse.moduo.main.presenter;

/**
 * VideoActivity更新UI的接口
 * Created by ssthouse on 2016/1/12.
 */
public interface VideoPresenter {

    /**
     * 开始calling
     */
    void startCalling();

    /**
     * 等待视频加载
     */
    void waitForVideoReady();

    /**
     * 解除Eventbus监听
     */
    void unRegister();
}

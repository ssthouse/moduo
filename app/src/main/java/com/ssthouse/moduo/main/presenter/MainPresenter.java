package com.ssthouse.moduo.main.presenter;

import android.content.Context;

/**
 * 更新UI的接口
 * Created by ssthouse on 2016/1/10.
 */
public interface MainPresenter {

    /**
     * 初始化设备列表
     */
    void initDeviceList();

    /**
     * 刷新设备列表
     */
    void refreshDeviceList();

    /**
     * 增加新设备
     * @param context
     */
    void addDevice(Context context);

    /**
     * 解除Eventbus监听
     */
    void unRegister();
}

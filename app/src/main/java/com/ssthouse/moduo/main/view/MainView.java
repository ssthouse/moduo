package com.ssthouse.moduo.main.view;

import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;

/**
 * Created by ssthouse on 2016/1/10.
 */
public interface MainView {

    /**
     * 刷新列表
     * @param event
     */
    void updateDeviceList(GetBoundDeviceEvent event);

    /**
     * 显示Dialog
     */
    void showDialog(String msg);

    /**
     * 隐藏Dialog
     */
    void dismissDialog();

    /**
     * 设置下拉刷新状态
     */
    void setRefreshEnable(boolean enable);
}

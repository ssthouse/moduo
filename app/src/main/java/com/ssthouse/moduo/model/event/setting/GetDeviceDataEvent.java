package com.ssthouse.moduo.model.event.setting;

import com.ssthouse.moduo.model.DeviceData;

/**
 * 获取初始设备数据事件y
 * Created by ssthouse on 2015/12/21.
 */
public class GetDeviceDataEvent {

    /**
     * 初始设备状态
     */
    private DeviceData initDeviceData;

    /**
     * 是否获取成功
     */
    private boolean isSuccess;

    /**
     * 获取失败的构造方法
     * @param isSuccess
     */
    public GetDeviceDataEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 获取成功的构造方法
     * @param initDeviceData
     * @param isSuccess
     */
    public GetDeviceDataEvent(boolean isSuccess, DeviceData initDeviceData) {
        this.initDeviceData = initDeviceData;
        this.isSuccess = isSuccess;
    }

    public DeviceData getInitDeviceData() {
        return initDeviceData;
    }

    public void setInitDeviceData(DeviceData initDeviceData) {
        this.initDeviceData = initDeviceData;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

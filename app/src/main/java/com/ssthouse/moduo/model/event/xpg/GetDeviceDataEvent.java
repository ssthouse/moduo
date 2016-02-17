package com.ssthouse.moduo.model.event.xpg;

import com.ssthouse.moduo.model.bean.device.DeviceData;

/**
 * 获取初始设备数据事件y
 * Created by ssthouse on 2015/12/21.
 */
public class GetDeviceDataEvent {

    /**
     * 初始设备状态
     */
    private DeviceData deviceData;

    /**
     * 是否获取成功
     */
    private boolean isSuccess;

    /**
     * 获取失败的构造方法
     *
     * @param isSuccess
     */
    public GetDeviceDataEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 获取成功的构造方法
     *
     * @param initDeviceData
     * @param isSuccess
     */
    public GetDeviceDataEvent(boolean isSuccess, DeviceData initDeviceData) {
        this.deviceData = initDeviceData;
        this.isSuccess = isSuccess;
    }

    public DeviceData getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(DeviceData deviceData) {
        this.deviceData = deviceData;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

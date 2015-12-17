package com.ssthouse.moduo.model;

/**
 * 一台设备有的所有数据
 * Created by ssthouse on 2015/12/17.
 */
public class Device {

    /**
     * 设备状态常量
     */
    public enum DeviceVideoState{
        ONLINE, OFFLINE
    }

    /**
     * 设备状态标识
     */
    private DeviceVideoState deviceVideoState;

    /**
     * 设备cid编号
     */
    private int cidNumber;

    /**
     * 构造方法
     * @param deviceVideoState
     */
    public Device(DeviceVideoState deviceVideoState) {
        this.deviceVideoState = deviceVideoState;
    }

    public DeviceVideoState getDeviceVideoState() {
        return deviceVideoState;
    }

    public void setDeviceVideoState(DeviceVideoState deviceVideoState) {
        this.deviceVideoState = deviceVideoState;
    }

    public int getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(int cidNumber) {
        this.cidNumber = cidNumber;
    }
}

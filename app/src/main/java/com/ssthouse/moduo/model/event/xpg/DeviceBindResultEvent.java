package com.ssthouse.moduo.model.event.xpg;

/**
 * 设备绑定结果Event
 * Created by ssthouse on 2015/12/20.
 */
public class DeviceBindResultEvent {

    private boolean isSuccess;

    private String did;

    /**
     * 构造方法
     *
     * @param isSuccess 是否登陆成功
     */
    public DeviceBindResultEvent(boolean isSuccess, String did) {
        this.isSuccess = isSuccess;
        this.did = did;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}

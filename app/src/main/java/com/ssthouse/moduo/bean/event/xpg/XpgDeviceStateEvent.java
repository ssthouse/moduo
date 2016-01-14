package com.ssthouse.moduo.bean.event.xpg;

/**
 * 设备状态回调事件
 * Created by ssthouse on 2015/12/20.
 */
public class XpgDeviceStateEvent {
    /**
     *
     */
    boolean isSuccess;

    private String did ;

    public XpgDeviceStateEvent(boolean isSuccess, String did) {
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

package com.ssthouse.moduo.model.event.xpg;

/**
 * 设备登陆状态回调事件
 * Created by ssthouse on 2015/12/20.
 */
public class XpgDeviceLoginEvent {
    /**
     *
     */
    boolean isSuccess;

    private String did ;

    public XpgDeviceLoginEvent(boolean isSuccess, String did) {
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

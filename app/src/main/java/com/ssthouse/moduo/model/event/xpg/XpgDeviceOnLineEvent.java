package com.ssthouse.moduo.model.event.xpg;

/**
 * XPG设备连接状态(是否在线)
 * Created by ssthouse on 2016/2/26.
 */
public class XpgDeviceOnLineEvent {

    private String did;

    private boolean success;

    public XpgDeviceOnLineEvent(String did, boolean success) {
        this.did = did;
        this.success = success;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

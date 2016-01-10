package com.ssthouse.moduo.bean.event.xpg;

/**
 * 设备状态回调事件
 * Created by ssthouse on 2015/12/20.
 */
public class XpgDeviceStateEvent {

    //todo---定义一个enum表示各个状态---再包含一个xpgdevice就可以包含所有的event了

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

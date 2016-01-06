package com.ssthouse.moduo.model.event.xpg;

import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取账号绑定设备的事件
 * Created by ssthouse on 2015/12/20.
 */
public class GetBoundDeviceEvent {

    boolean isSuccess;

    /**
     * 获取失败的狗构造方法
     *
     * @param isSuccess
     */
    public GetBoundDeviceEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.xpgDeviceList = new ArrayList<>();
    }

    /**
     * 获取到的账号绑定的设备列表
     */
    private List<XPGWifiDevice> xpgDeviceList;

    /**
     * 获取成功的构造方法
     *
     * @param isSuccess
     * @param xpgDeviceList
     */
    public GetBoundDeviceEvent(boolean isSuccess, List<XPGWifiDevice> xpgDeviceList) {
        this.isSuccess = isSuccess;
        this.xpgDeviceList = xpgDeviceList;
    }

    //getter---and---setter-------------------------------------------------------------------------

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<XPGWifiDevice> getXpgDeviceList() {
        return xpgDeviceList;
    }

    public void setXpgDeviceList(List<XPGWifiDevice> xpgDeviceList) {
        this.xpgDeviceList = xpgDeviceList;
    }
}

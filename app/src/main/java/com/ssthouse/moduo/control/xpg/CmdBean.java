package com.ssthouse.moduo.control.xpg;

import android.util.Base64;

/**
 * 包含一个控制命令的bean
 * Created by ssthouse on 2016/3/3.
 */
public class CmdBean {
    private byte deviceType;
    private byte deviceNumber;
    private byte paramKey;
    private byte paramValue;

    public CmdBean(byte deviceType, byte deviceNumber, byte paramKey, byte paramValue) {
        this.deviceType = deviceType;
        this.deviceNumber = deviceNumber;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    //获取数据
    public String getValueStr() {
        byte data[] = new byte[4];
        data[0] = deviceType;
        data[1] = deviceNumber;
        data[2] = paramKey;
        data[3] = paramValue;
        return Base64.encodeToString(data, Base64.NO_CLOSE);
    }

    //getter-------------------------setter--------------------------
    public byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(byte deviceType) {
        this.deviceType = deviceType;
    }

    public byte getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(byte deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public byte getParamKey() {
        return paramKey;
    }

    public void setParamKey(byte paramKey) {
        this.paramKey = paramKey;
    }

    public byte getParamValue() {
        return paramValue;
    }

    public void setParamValue(byte paramValue) {
        this.paramValue = paramValue;
    }
}

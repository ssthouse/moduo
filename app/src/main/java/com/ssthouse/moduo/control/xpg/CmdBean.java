package com.ssthouse.moduo.control.xpg;

import android.util.Base64;

/**
 * 机智云相关:
 * 包含一个控制命令的bean, 用于封装单个的控制命令数据
 * Created by ssthouse on 2016/3/3.
 */
public class CmdBean {
    //四个字节的数据
    private byte deviceType;
    private byte deviceNumber;
    private byte paramKey;
    private byte paramValue;

    //设备类型
    public enum DeviceType {
        NONE((byte) 0),
        TV((byte) 1),
        AC((byte) 2),
        END((byte) 3);

        public byte value;

        DeviceType(byte value) {
            this.value = value;
        }
    }

    //设备编号
    //操作的参数
    public enum DeviceParam {
        NONE((byte) 0),
        ONOFF((byte) 1),
        TV_VOLUME((byte) 2),
        TV_CHANNEL((byte) 3),
        TV_SRC((byte) 4),
        AC_ONOFF((byte) 5),
        AC_TEMP((byte) 6),
        AC_MODE((byte) 7),
        END((byte) 8);
        public byte value;

        DeviceParam(byte value) {
            this.value = value;
        }
    }
    //操作参数的值

    public CmdBean(byte deviceType, byte deviceNumber, byte paramKey, byte paramValue) {
        this.deviceType = deviceType;
        this.deviceNumber = deviceNumber;
        this.paramKey = paramKey;
        this.paramValue = paramValue;
    }

    //获取实例
    public static CmdBean getInstance(byte deviceType, byte deviceNumber, byte paramKey, byte paramValue) {
        return new CmdBean(deviceType, deviceNumber, paramKey, paramValue);
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

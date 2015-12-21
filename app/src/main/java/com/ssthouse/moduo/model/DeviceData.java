package com.ssthouse.moduo.model;

import java.io.Serializable;

/**
 * 包含一个设备可读写的所有数据:
 * 可序列化
 * Created by ssthouse on 2015/12/21.
 */
public class DeviceData implements Serializable {

    /**
     * 数据的常量key
     */
    public interface DeviceCons {
        //当前温度
        String KEY_TEMPERATURE = "temperature";
        //当前湿度
        String KEY_HUMIDITY = "humidity";
        //当前亮度
        String KEY_LUMINANCE = "luminance";
        //视频开关
        String KEY_VIDEO = "video";
        //音频开关
        String KEY_AUDIO = "audio";
        //电量
        String KEY_POWER = "power";
        //硬件版本
        String KEY_HW_VERSION = "hw_version";
        //软件版本
        String KEY_SW_VERSION = "sw_version";
        //x轴 头部
        String KEY_X_HEAD = "x_head";
        //y轴 头部
        String KEY_Y_HEAD = "y_head";
        //z轴 头部
        String KEY_Z_HEAD = "z_head";
        //x轴 身体
        String KEY_X_BODY = "x_body";
        //y轴 身体
        String KEY_Y_BODY = "y_body";
        //z轴 身体
        String KEY_Z_BODY = "z_body";
        //扩展类型 命令
        String KEY_CTRL_CMD = "ctrl_cmd";
        //扩展类型 数据
        String KEY_CTRL_DATA = "ctrl_data";
        //cmd类型
        String KEY_CMD = "cmd";
    }

    /**
     * 温度
     */
    private int temperature;

    /**
     * 湿度
     */
    private double humidity;

    /**
     * 传入数据的构造方法
     *
     * @param temperature
     * @param humidity
     */
    public DeviceData(int temperature, double humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    //getter---and---setter-------------------------------------------------------------------------

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}

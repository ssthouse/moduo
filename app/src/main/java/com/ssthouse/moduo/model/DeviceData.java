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
        //温度
        String KEY_TEMPERATURE = "temperature";
        //湿度
        String KEY_HUMIDITY = "humidity";
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

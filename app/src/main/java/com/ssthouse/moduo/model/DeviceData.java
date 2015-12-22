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
    private int humidity;
    /**
     * 亮度
     */
    private int luminance;
    /**
     * 电量
     */
    private int power;
    /**
     * 硬件版本
     * todo
     */
    private String hwVersion;
    /**
     * 软件版本
     * todo
     */
    private String swVersion;
    /**
     * 视频标志位
     */
    private int video;
    /**
     * 音频标志位
     */
    private int audio;
    /**
     * 头部
     */
    private int xHead;
    private int yHead;
    private int zHead;
    /**
     * 身体
     */
    private int xBody;
    private int yBody;
    private int zBody;

    /**
     * todo---测试构造方法
     * 传入数据的构造方法
     *
     * @param temperature
     * @param humidity
     */
    public DeviceData(int temperature, int humidity) {
        this.temperature = temperature;
        this.humidity = humidity;
    }

    /**
     * 传入所有数据的构造方法
     *
     * @param temperature
     * @param humidity
     * @param luminance
     * @param power
     * @param video
     * @param audio
     * @param xHead
     * @param yHead
     * @param zHead
     * @param xBody
     * @param yBody
     * @param zBody
     */
    public DeviceData(int temperature, int humidity, int luminance, int power, int video, int audio, int xHead, int yHead, int zHead, int xBody, int yBody, int zBody) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminance = luminance;
        this.power = power;
        this.video = video;
        this.audio = audio;
        this.xHead = xHead;
        this.yHead = yHead;
        this.zHead = zHead;
        this.xBody = xBody;
        this.yBody = yBody;
        this.zBody = zBody;
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

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getLuminance() {
        return luminance;
    }

    public void setLuminance(int luminance) {
        this.luminance = luminance;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public int getxHead() {
        return xHead;
    }

    public void setxHead(int xHead) {
        this.xHead = xHead;
    }

    public int getyHead() {
        return yHead;
    }

    public void setyHead(int yHead) {
        this.yHead = yHead;
    }

    public int getzHead() {
        return zHead;
    }

    public void setzHead(int zHead) {
        this.zHead = zHead;
    }

    public int getxBody() {
        return xBody;
    }

    public void setxBody(int xBody) {
        this.xBody = xBody;
    }

    public int getyBody() {
        return yBody;
    }

    public void setyBody(int yBody) {
        this.yBody = yBody;
    }

    public int getzBody() {
        return zBody;
    }

    public void setzBody(int zBody) {
        this.zBody = zBody;
    }
}

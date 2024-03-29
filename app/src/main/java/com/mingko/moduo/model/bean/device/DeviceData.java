package com.mingko.moduo.model.bean.device;

import android.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mingko.moduo.control.util.ByteUtils;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import timber.log.Timber;

/**
 * 包含一个设备可读写的所有数据:
 * 可序列化
 * Created by ssthouse on 2015/12/21.
 */
public class DeviceData implements Serializable {

    /**
     * 数据的常量key
     */
    public interface DeviceConstant {

        /**
         * 服务器返回数据中的常量
         */
        String DATA = "data";
        String CMD = "cmd";
        //魔哆的实体key
        String ENTITY0 = "entity0";

        //当前温度
        String KEY_TEMPERATURE = "temperature";
        //当前湿度
        String KEY_HUMIDITY = "humidity";
        //当前亮度
        String KEY_LUMINANCE = "luminance";
        //视频开关---bool值
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
    }

    /**
     * 数据所属的设备
     */
    private String did;
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
     * 视频标志位
     */
    private boolean video;
    /**
     * 音频标志位
     */
    private int audio;
    /**
     * 电量
     */
    private int power;
    /**
     * 硬件版本
     */
    private byte[] hwVersion;
    /**
     * 软件版本
     */
    private byte[] swVersion;
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
     * 控制 命令
     */
    private byte[] ctrlCmd;
    /**
     * 控制 数据
     */
    private byte[] ctrlData;

    /**
     * 传入所有数据的构造方法
     *
     * @param did   数据所属的设备
     * @param temperature   温度
     * @param humidity  湿度
     * @param luminance 亮度
     * @param video 视频标志位
     * @param audio 音频标志位
     * @param power 电量
     * @param hwVersion 硬件版本
     * @param swVersion 软件版本
     * @param xHead x轴 头部
     * @param yHead y轴 头部
     * @param zHead z轴 头部
     * @param xBody x轴 身体
     * @param yBody y轴 身体
     * @param zBody z轴 身体
     * @param ctrlCmd   控制 命令
     * @param ctrlData  控制 数据
     */
    private DeviceData(String did, int temperature, int humidity, int luminance, boolean video,
                       int audio, int power, byte[] hwVersion, byte[] swVersion, int xHead, int yHead,
                      int zHead, int xBody, int yBody, int zBody, byte[] ctrlCmd, byte[] ctrlData) {
        this.did = did;
        this.temperature = temperature;
        this.humidity = humidity;
        this.luminance = luminance;
        this.video = video;
        this.audio = audio;
        this.power = power;
        this.hwVersion = hwVersion;
        this.swVersion = swVersion;
        this.xHead = xHead;
        this.yHead = yHead;
        this.zHead = zHead;
        this.xBody = xBody;
        this.yBody = yBody;
        this.zBody = zBody;
        this.ctrlCmd = ctrlCmd;
        this.ctrlData = ctrlData;
    }

    /**
     * 根据设备返回map得到一个DeviceData
     *
     * @return 设备的数据
     */
    public static DeviceData getDeviceData(XPGWifiDevice device,
                                           ConcurrentHashMap<String, Object> dataMap) {
        //解析json数据
        JsonParser parser = new JsonParser();
        JsonObject jsonData = (JsonObject) parser.parse("" + dataMap.get(DeviceConstant.DATA));
        JsonObject dataObject = jsonData.get(DeviceConstant.ENTITY0).getAsJsonObject();
        //从jsonObject中获取数据
        int temperature = dataObject.get(DeviceConstant.KEY_TEMPERATURE).getAsInt();
        int humidity = dataObject.get(DeviceConstant.KEY_HUMIDITY).getAsInt();
        int luminance = dataObject.get(DeviceConstant.KEY_LUMINANCE).getAsInt();
        boolean video = dataObject.get(DeviceConstant.KEY_VIDEO).getAsBoolean();
        int audio = dataObject.get(DeviceConstant.KEY_AUDIO).getAsInt();
        int power = dataObject.get(DeviceConstant.KEY_POWER).getAsInt();
        byte[] hwVersion = Base64.decode(dataObject.get(DeviceConstant.KEY_HW_VERSION).getAsString(), Base64.DEFAULT);
        byte[] swVersion = Base64.decode(dataObject.get(DeviceConstant.KEY_SW_VERSION).getAsString(), Base64.DEFAULT);
        int xHead = dataObject.get(DeviceConstant.KEY_X_HEAD).getAsInt();
        int yHead = dataObject.get(DeviceConstant.KEY_Y_HEAD).getAsInt();
        int zHead = dataObject.get(DeviceConstant.KEY_Z_HEAD).getAsInt();
        int xBody = dataObject.get(DeviceConstant.KEY_X_BODY).getAsInt();
        int yBody = dataObject.get(DeviceConstant.KEY_Y_BODY).getAsInt();
        int zBody = dataObject.get(DeviceConstant.KEY_Z_BODY).getAsInt();
        byte[] ctrlCmd = Base64.decode(dataObject.get(DeviceConstant.KEY_CTRL_CMD).getAsString(), Base64.DEFAULT);
        byte[] ctrlData = Base64.decode(dataObject.get(DeviceConstant.KEY_CTRL_DATA).getAsString(), Base64.DEFAULT);
        //查看数据
        Timber.e("温度：" + temperature);
        Timber.e("湿度：" + humidity);
        Timber.e("亮度：" + luminance);
        Timber.e("电量：" + power);
        Timber.e("视频开关：" + video);
        Timber.e("音频：" + audio);
        Timber.e("硬件版本：" + ByteUtils.bytes2HexString(hwVersion));
        Timber.e("软件版本：" + ByteUtils.bytes2HexString(swVersion));
        Timber.e("x轴 头部：" + xHead);
        Timber.e("y轴 头部：" + yHead);
        Timber.e("z轴 头部：" + zHead);
        Timber.e("x轴 身体：" + xBody);
        Timber.e("y轴 身体：" + yBody);
        Timber.e("z轴 身体：" + zBody);
        Timber.e("控制 命令：" + dataObject.get(DeviceConstant.KEY_CTRL_CMD).getAsString());
        Timber.e("控制 数据：" + ByteUtils.bytes2HexString(ctrlData));
        //返回解析出的数据
        return new DeviceData(device.getDid(), temperature, humidity, luminance, video, audio, power,
                hwVersion, swVersion, xHead, yHead, zHead, xBody, yBody, zBody, ctrlCmd, ctrlData);
    }

    //getter---and---setter-------------------------------------------------------------------------

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
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

    public boolean getVideo() {
        return video;
    }

    public void setVideo(boolean video) {
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

    public byte[] getHwVersion() {
        return hwVersion;
    }

    public void setHwVersion(byte[] hwVersion) {
        this.hwVersion = hwVersion;
    }

    public byte[] getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(byte[] swVersion) {
        this.swVersion = swVersion;
    }

    public byte[] getCtrlCmd() {
        return ctrlCmd;
    }

    public void setCtrlCmd(byte[] ctrlCmd) {
        this.ctrlCmd = ctrlCmd;
    }

    public byte[] getCtrlData() {
        return ctrlData;
    }

    public void setCtrlData(byte[] ctrlData) {
        this.ctrlData = ctrlData;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}

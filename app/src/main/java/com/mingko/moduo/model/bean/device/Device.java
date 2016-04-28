package com.mingko.moduo.model.bean.device;

import android.content.Context;
import android.util.Base64;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.mingko.moduo.control.xpg.CmdBean;
import com.mingko.moduo.model.bean.ModuoInfo;
import com.mingko.moduo.control.xpg.SettingManager;
import com.mingko.moduo.model.cons.xpg.JsonKeys;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import timber.log.Timber;

/**
 * 一台设备有的所有数据
 * Created by ssthouse on 2015/12/17.
 */
public class Device implements Serializable {

    /*
    机智云sdk数据
     */
    private XPGWifiDevice xpgWifiDevice;

    /*
    视频sdk数据
     */
    private String videoCidNumber;
    private String videoUsername;
    private String videoPassword;
    //// FIXME: 2016/4/27 不知道什么用的参数
    private StreamerPresenceState streamerPresenceState;

    private Device(XPGWifiDevice xpgWifiDevice, ModuoInfo moduoInfo) {
        this.xpgWifiDevice = xpgWifiDevice;
        this.videoCidNumber = moduoInfo.getCid();
        this.videoUsername = moduoInfo.getVideoUsername();
        this.videoPassword = moduoInfo.getVideoPassword();
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
    }

    /**
     * Device数据是否完整
     *
     * @return 完整返回true, 否则返回false
     */
    public boolean isValid(){
        return !(videoCidNumber == null || videoCidNumber.isEmpty() ||
                videoUsername == null || videoUsername.isEmpty() ||
                videoPassword == null || videoPassword.isEmpty());
    }

    /**
     * 获取本地参数组成的设备
     *
     * @return 当前对象
     */
    public static Device getLocalDevice(Context context, XPGWifiDevice xpgWifiDevice) {
        ModuoInfo moduoInfo = SettingManager.getInstance(context).getCurrentModuoInfo();
        return new Device(xpgWifiDevice, moduoInfo);
    }

    // =================================================================
    //
    // 设备控制相关指令
    //
    // =================================================================

    /**
     * 发送指令.
     * 抽象出主体逻辑的命令方法
     * 发送对象格式：{"entity0":{"x_head":30}, "cmd":1}
     *      "entity0" 魔哆项目代表的实体Key
     *      "x_head" 头部X轴坐标为30
     *      "cmd" 1表示写入
     *
     * @param key   属性
     * @param value 值
     */
    public void cWrite(String key, Object value) {
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(key, value);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
            Timber.e("send_data:\t" + jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //开启设备的video数据点---打开video
    public void cWriteVideo(int value) {
        if (xpgWifiDevice == null) {
            Timber.e("当前XpgWifiDevice为空");
            return;
        }
        if (value == 0) {
            cWrite(JsonKeys.VIDEO, false);
        } else if (value == 1) {
            cWrite(JsonKeys.VIDEO, true);
        }
    }

    /**
     * 改变头部
     *
     * @param xHead x轴 头部
     * @param yHead y轴 头部
     * @param zHead z轴 头部
     */
    public void cWriteHead(int xHead, int yHead, int zHead) {
        if (xpgWifiDevice == null) {
            Timber.e("当前XpgWifiDevice为空");
            return;
        }
//        Timber.e("control moduo: △X:%d    △Y:%d    △Z:%d    ", xHead, yHead, zHead);
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(JsonKeys.X_HEAD, xHead);
            jsonParam.put(JsonKeys.Y_HEAD, yHead);
            jsonParam.put(JsonKeys.Z_HEAD, zHead);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
//            Timber.e(jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变身体
     *
     * @param xBody x轴 身体
     * @param yBody y轴 身体
     * @param zBody z轴 身体
     */
    public void cWriteBody(int xBody, int yBody, int zBody) {
        if (xpgWifiDevice == null) {
            Timber.e("当前XpgWifiDevice为空 writeBody失败");
            return;
        }
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(JsonKeys.X_BODY, xBody);
            jsonParam.put(JsonKeys.Y_BODY, yBody);
            jsonParam.put(JsonKeys.Z_BODY, zBody);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
            Timber.e(jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //发送控制命令
    public void cWriteCmd(byte device, byte no, byte param, byte value) {
        if (xpgWifiDevice == null) {
            Timber.e("当前XpgWifiDevice为空, writeCmd失败");
            return;
        }
        byte data[] = new byte[4];
        data[0] = device;
        data[1] = no;
        data[2] = param;
        data[3] = value;
        String cmdStr = Base64.encodeToString(data, Base64.NO_CLOSE);
        Timber.e("cmdStr: " + cmdStr);
        cWrite(JsonKeys.CTRL_CMD, cmdStr);
    }

    //发送cmd_ctrl命令
    public void cWriteCmdCtrl(CmdBean cmdBean) {
        if (xpgWifiDevice == null || cmdBean == null) {
            Timber.e("CmdCtrl发送失败, 设备或数据为空!");
            return;
        }
        cWrite(JsonKeys.CTRL_CMD, cmdBean.getValueStr());
    }

    //getter---and---setter-------------------------------------------------------------------------

    public XPGWifiDevice getXpgWifiDevice() {
        return xpgWifiDevice;
    }

    public String getVideoCidNumber() {
        return videoCidNumber;
    }

    public String getVideoUsername() {
        return videoUsername;
    }

    public String getVideoPassword() {
        return videoPassword;
    }

}

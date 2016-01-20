package com.ssthouse.moduo.bean.device;

import android.content.Context;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ssthouse.moduo.bean.ModuoInfo;
import com.ssthouse.moduo.main.control.util.PreferenceHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.io.Serializable;

/**
 * 一台设备有的所有数据
 * Created by ssthouse on 2015/12/17.
 */
public class Device implements Serializable {

    /*
    视频sdk数据
     */
    private StreamerPresenceState streamerPresenceState;
    private String videoUsername;
    private String videoPassword;
    private String videoCidNumber;

    /*
    机智云sdk数据
     */
    private XPGWifiDevice xpgWifiDevice;

    /**
     * 构造方法:
     * <p>
     * 传入一个已经绑定的机智云设备
     * 自动获取视频sdk参数
     *
     * @param context       上下文
     * @param xpgWifiDevice
     */
    public Device(Context context, XPGWifiDevice xpgWifiDevice) {
        this.xpgWifiDevice = xpgWifiDevice;
        //默认状态
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
        //todo---参数检查---根据给的机智云设备---在preference中获取视频sdk参数
        String did = xpgWifiDevice.getDid();
        this.videoCidNumber = PreferenceHelper.getInstance(context).getCidNumber(did);
        this.videoUsername = PreferenceHelper.getInstance(context).getUsername(did);
        this.videoPassword = PreferenceHelper.getInstance(context).getPassword(did);
    }

    public Device(String videoCidNumber, String videoUsername, String videoPassword, XPGWifiDevice xpgWifiDevice) {
        this.videoCidNumber = videoCidNumber;
        this.videoUsername = videoUsername;
        this.videoPassword = videoPassword;
        this.xpgWifiDevice = xpgWifiDevice;
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
    }

    /**
     * 获取本地参数组成的设备
     *
     * @return
     */
    public static Device getLocalDevice(Context context, XPGWifiDevice xpgWifiDevice) {
        ModuoInfo moduoInfo = SettingManager.getInstance(context).getCurrentModuoInfo();
        return new Device(moduoInfo.getCid(),
                moduoInfo.getVideoUsername(),
                moduoInfo.getVideoPassword(),
                xpgWifiDevice);
    }

    //getter---and---setter-------------------------------------------------------------------------
    public StreamerPresenceState getStreamerPresenceState() {
        return streamerPresenceState;
    }

    public void setStreamerPresenceState(StreamerPresenceState streamerPresenceState) {
        this.streamerPresenceState = streamerPresenceState;
    }

    public String getVideoCidNumber() {
        return videoCidNumber;
    }

    public void setVideoCidNumber(String videoCidNumber) {
        this.videoCidNumber = videoCidNumber;
    }

    public String getVideoUsername() {
        return videoUsername;
    }

    public void setVideoUsername(String videoUsername) {
        this.videoUsername = videoUsername;
    }

    public String getVideoPassword() {
        return videoPassword;
    }

    public void setVideoPassword(String videoPassword) {
        this.videoPassword = videoPassword;
    }

    public XPGWifiDevice getXpgWifiDevice() {
        return xpgWifiDevice;
    }

    public void setXpgWifiDevice(XPGWifiDevice xpgWifiDevice) {
        this.xpgWifiDevice = xpgWifiDevice;
    }
}

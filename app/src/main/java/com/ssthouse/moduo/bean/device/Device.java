package com.ssthouse.moduo.bean.device;

import android.content.Context;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ssthouse.moduo.main.control.util.PreferenceHelper;
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
    /**
     * 设备状态标识---使用的是sdk提供的枚举
     */
    private StreamerPresenceState streamerPresenceState;

    /**
     * 视频SDK---设备cid编号
     */
    private long videoCidNumber;

    /**
     * 视频SDK---设备用户名
     */
    private String videoUsername;

    /**
     * 视频SDK---设备密码
     */
    private String videoPassword;

    /*
    机智云sdk数据
     */
    /**
     * 机智云设备
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

    //getter---and---setter-------------------------------------------------------------------------
    public StreamerPresenceState getStreamerPresenceState() {
        return streamerPresenceState;
    }

    public void setStreamerPresenceState(StreamerPresenceState streamerPresenceState) {
        this.streamerPresenceState = streamerPresenceState;
    }

    public long getVideoCidNumber() {
        return videoCidNumber;
    }

    public void setVideoCidNumber(long videoCidNumber) {
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

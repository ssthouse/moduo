package com.ssthouse.moduo.bean.device;

import android.content.Context;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ssthouse.moduo.control.util.PreferenceHelper;
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
     * TODO---设备cid编号
     */
    private long cidNumber = 50000072;

    /**
     * TODO---设备用户名
     */
    private String username = "admin";

    /**
     * TODO---设备密码
     */
    private String password = "admin";

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
        this.cidNumber = PreferenceHelper.getInstance(context).getCidNumber(did);
        this.username = PreferenceHelper.getInstance(context).getUsername(did);
        this.password = PreferenceHelper.getInstance(context).getPassword(did);
    }

    //getter---and---setter-------------------------------------------------------------------------
    public StreamerPresenceState getStreamerPresenceState() {
        return streamerPresenceState;
    }

    public void setStreamerPresenceState(StreamerPresenceState streamerPresenceState) {
        this.streamerPresenceState = streamerPresenceState;
    }

    public long getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(long cidNumber) {
        this.cidNumber = cidNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public XPGWifiDevice getXpgWifiDevice() {
        return xpgWifiDevice;
    }

    public void setXpgWifiDevice(XPGWifiDevice xpgWifiDevice) {
        this.xpgWifiDevice = xpgWifiDevice;
    }
}

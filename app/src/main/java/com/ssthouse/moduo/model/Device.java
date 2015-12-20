package com.ssthouse.moduo.model;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

/**
 * 一台设备有的所有数据
 * Created by ssthouse on 2015/12/17.
 */
public class Device {

    /*
    视频sdk数据
    TODO---暂时默认为官网给的设备, 当时候换成二维码里面扫描得到的数据
     */
    /**
     * 设备状态标识
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
     * 构造方法
     *
     * @param cidNumber cid号码
     * @param username 视频用户名
     * @param password 视频密码
     */
    public Device(long cidNumber, String username, String password) {
        this.cidNumber = cidNumber;
        this.username = username;
        this.password = password;
        //默认状态
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
    }

    /**
     * TODO
     * 构造方法:
     *
     * 使用官网视频接口
     * 传入一个已经绑定的机智云设备
     * @param xpgWifiDevice
     */
    public Device(XPGWifiDevice xpgWifiDevice) {
        this.xpgWifiDevice = xpgWifiDevice;
        //默认状态
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
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

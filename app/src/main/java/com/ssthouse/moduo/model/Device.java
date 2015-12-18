package com.ssthouse.moduo.model;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;

/**
 * 一台设备有的所有数据
 * Created by ssthouse on 2015/12/17.
 */
public class Device {

    /**
     * 设备状态标识
     */
    private StreamerPresenceState streamerPresenceState;

    /**
     * 设备cid编号
     */
    private long cidNumber;

    /**
     * 设备用户名
     */
    private String username;

    /**
     * 设备密码
     */
    private String password;

    /**
     * 构造方法
     * @param cidNumber
     * @param username
     * @param password
     */
    public Device(long cidNumber, String username, String password) {
        this.cidNumber = cidNumber;
        this.username = username;
        this.password = password;
        //默认状态
        this.streamerPresenceState = StreamerPresenceState.OFFLINE;
    }

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
}

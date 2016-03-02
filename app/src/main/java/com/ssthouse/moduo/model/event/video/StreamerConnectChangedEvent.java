package com.ssthouse.moduo.model.event.video;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;

/**
 * 采集端链接状态变化事件
 * Created by ssthouse on 2015/12/17.
 */
public class StreamerConnectChangedEvent {

    /**
     * 当前设备cid号码
     */
    private long cidNumber;

    /**
     * 当前设备状态
     */
    private StreamerPresenceState state;

    /**
     * 构造方法
     *
     * @param cidNumber
     * @param state
     */
    public StreamerConnectChangedEvent(long cidNumber, StreamerPresenceState state) {
        this.cidNumber = cidNumber;
        this.state = state;
    }

    public StreamerPresenceState getState() {
        return state;
    }

    public void setState(StreamerPresenceState state) {
        this.state = state;
    }

    public long getCidNumber() {
        return cidNumber;
    }

    public void setCidNumber(long cidNumber) {
        this.cidNumber = cidNumber;
    }
}

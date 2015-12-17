package com.ssthouse.moduo.model.event.video;

import com.ichano.rvs.viewer.constant.StreamerPresenceState;

/**
 * 采集端链接状态变化事件
 * Created by ssthouse on 2015/12/17.
 */
public class StreamerConnectChangedEvent {

    private StreamerPresenceState state;

    public StreamerConnectChangedEvent(StreamerPresenceState state) {
        this.state = state;
    }

    public StreamerPresenceState getState() {
        return state;
    }

    public void setState(StreamerPresenceState state) {
        this.state = state;
    }
}

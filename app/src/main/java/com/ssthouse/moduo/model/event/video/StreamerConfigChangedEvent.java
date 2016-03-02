package com.ssthouse.moduo.model.event.video;

import com.ichano.rvs.viewer.constant.StreamerConfigState;

/**
 * 采集端设置状态变化事件
 * Created by ssthouse on 2015/12/17.
 */
public class StreamerConfigChangedEvent {

    public StreamerConfigState state;

    public StreamerConfigChangedEvent(StreamerConfigState state) {
        this.state = state;
    }

    public StreamerConfigState getState() {
        return state;
    }

    public void setState(StreamerConfigState state) {
        this.state = state;
    }
}

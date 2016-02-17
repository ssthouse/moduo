package com.ssthouse.moduo.model.event.video;

import com.ichano.rvs.viewer.constant.RvsSessionState;

/**
 * 采集端和客户端回话状态变化事件
 * Created by ssthouse on 2015/12/17.
 */
public class SessionStateEvent {

    private RvsSessionState sessionState;

    public SessionStateEvent(RvsSessionState sessionState) {
        this.sessionState = sessionState;
    }

    public RvsSessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(RvsSessionState sessionState) {
        this.sessionState = sessionState;
    }
}

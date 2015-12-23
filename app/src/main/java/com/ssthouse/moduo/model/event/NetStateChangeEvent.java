package com.ssthouse.moduo.model.event;

/**
 * 网络状态变化事件
 * Created by ssthouse on 2015/12/23.
 */
public class NetStateChangeEvent {

    /**
     * 网络状态枚举
     */
    public enum NetworkState{
        NONE, WIFI, MOBILE
    }

    /**
     * 网络状态
     */
    private NetworkState networkState;

    /**
     * 网络状态变化事件
     * @param networkState
     */
    public NetStateChangeEvent(NetworkState networkState) {
        this.networkState = networkState;
    }

    public NetworkState getNetworkState() {
        return networkState;
    }

    public void setNetworkState(NetworkState networkState) {
        this.networkState = networkState;
    }
}

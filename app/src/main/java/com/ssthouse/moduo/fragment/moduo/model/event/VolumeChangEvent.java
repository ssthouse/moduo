package com.ssthouse.moduo.fragment.moduo.model.event;

/**
 * 音量变化事件
 * Created by ssthouse on 2016/1/26.
 */
public class VolumeChangEvent {
    int volumnLevel;

    public VolumeChangEvent(int volumnLevel) {
        this.volumnLevel = volumnLevel;
    }

    public int getVolumnLevel() {
        return volumnLevel;
    }

    public void setVolumnLevel(int volumnLevel) {
        this.volumnLevel = volumnLevel;
    }
}

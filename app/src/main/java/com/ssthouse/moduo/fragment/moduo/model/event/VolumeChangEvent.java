package com.ssthouse.moduo.fragment.moduo.model.event;

/**
 * 音量变化事件
 * Created by ssthouse on 2016/1/26.
 */
public class VolumeChangEvent {
    int volumeLevel;

    public VolumeChangEvent(int volumeLevel) {
        this.volumeLevel = volumeLevel;
    }

    public int getVolumeLevel() {
        return volumeLevel;
    }

    public void setVolumeLevel(int volumeLevel) {
        this.volumeLevel = volumeLevel;
    }
}

package com.ssthouse.moduo.model.event.video;

/**
 * 视频流准备完毕
 * Created by ssthouse on 2016/1/13.
 */
public class VideoReadyEvent {

    private boolean success;

    public VideoReadyEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

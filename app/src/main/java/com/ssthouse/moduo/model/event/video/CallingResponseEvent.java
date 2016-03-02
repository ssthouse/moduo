package com.ssthouse.moduo.model.event.video;

/**
 * 拨号结果
 * Created by ssthouse on 2016/1/13.
 */
public class CallingResponseEvent {

    private boolean isSuccess;

    public CallingResponseEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

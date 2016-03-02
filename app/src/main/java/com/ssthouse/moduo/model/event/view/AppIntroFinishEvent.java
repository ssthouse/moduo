package com.ssthouse.moduo.model.event.view;

/**
 * 完成APP介绍事件
 * Created by ssthouse on 2016/1/23.
 */
public class AppIntroFinishEvent {

    private boolean isSuccess;

    public AppIntroFinishEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

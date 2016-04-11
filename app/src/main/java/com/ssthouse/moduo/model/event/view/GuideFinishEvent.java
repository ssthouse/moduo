package com.ssthouse.moduo.model.event.view;

/**
 * GuideActivity结束事件
 * Created by ssthouse on 2016/2/25.
 */
public class GuideFinishEvent {

    private boolean success;

    public GuideFinishEvent(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

package com.ssthouse.moduo.model.event.video;

/**
 * viewer登陆结果回调
 * Created by ssthouse on 2015/12/17.
 */
public class ViewerLoginResultEvent {

    private boolean isSuccess;

    public ViewerLoginResultEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

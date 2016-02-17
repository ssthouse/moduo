package com.ssthouse.moduo.model.event.xpg;

/**
 * 验证码发送结果回调
 * Created by ssthouse on 2015/12/19.
 */
public class AuthCodeSendResultEvent {

    boolean isSuccess;

    public AuthCodeSendResultEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

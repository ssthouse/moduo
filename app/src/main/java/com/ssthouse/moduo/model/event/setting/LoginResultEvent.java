package com.ssthouse.moduo.model.event.setting;

/**
 * 登陆事件
 * Created by ssthouse on 2015/12/19.
 */
public class LoginResultEvent {

    boolean isSuccess;

    public LoginResultEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}

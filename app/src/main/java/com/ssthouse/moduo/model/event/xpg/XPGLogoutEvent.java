package com.ssthouse.moduo.model.event.xpg;

/**
 * 登出event
 * Created by ssthouse on 2016/1/21.
 */
public class XPGLogoutEvent {

    private boolean isSuccess;
    private int errorCode;

    public XPGLogoutEvent(boolean isSuccess, int errorCode) {
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

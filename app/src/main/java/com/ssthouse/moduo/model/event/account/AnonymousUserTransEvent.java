package com.ssthouse.moduo.model.event.account;

/**
 * 匿名用户转普通用户
 * Created by ssthouse on 2016/1/20.
 */
public class AnonymousUserTransEvent {

    private boolean isSuccess;

    private int errorCode;


    public AnonymousUserTransEvent(boolean isSUccess, int errorCode) {
        this.isSuccess = isSUccess;
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

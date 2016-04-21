package com.mingko.moduo.model.event.account;

import java.util.List;

/**
 * 匿名用户转普通用户
 * Created by ssthouse on 2016/1/20.
 */
public class AnonymousUserTransEvent {

    private boolean isSuccess;

    private int errorCode;

    /**
     * 匿名用户转普通用户
     *
     * @param isSuccess //是否转换成功
     * @param errorCode //错误代码
     */
    public AnonymousUserTransEvent(boolean isSuccess, int errorCode) {
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

package com.mingko.moduo.model.event.account;

/**
 * 匿名用户转普通用户
 * Created by ssthouse on 2016/1/20.
 */
public class AnonymousUserTransEvent {

    private boolean isSuccess;  //是否转换成功

    private int errorCode;  //错误代码

    /**
     * 构造方法
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

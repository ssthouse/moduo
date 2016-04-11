package com.ssthouse.moduo.model.event.account;

/**
 * 机智云注册事件:
 * 如果注册成功, 会带有token 和 uid 否则为空
 * Created by ssthouse on 2015/12/19.
 */
public class RegisterResultEvent {

    private boolean isSuccess;

    private int errorCode;

    /**
     * 注册失败构造方法
     *
     * @param isSuccess
     */
    public RegisterResultEvent(boolean isSuccess, int errorCode) {
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
        uid = "";
        token = "";
    }

    /*
    可选项, 只有当isSuccess为true时有效
     */
    private String uid;
    private String token;

    /**
     * 注册成功构造方法
     *
     * @param isSuccess
     * @param uid
     * @param token
     */
    public RegisterResultEvent(boolean isSuccess, String uid, String token) {
        this.isSuccess = isSuccess;
        this.uid = uid;
        this.token = token;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

package com.ssthouse.moduo.model.event.xpg;

/**
 * 登陆事件
 * Created by ssthouse on 2015/12/19.
 */
public class XPGLoginResultEvent {

    boolean isSuccess;

    /**
     * 登陆失败回调
     *
     * @param isSuccess
     */
    public XPGLoginResultEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
        this.uid = "";
        this.token = "";
    }

    /*
    登陆成功才有的数据
     */
    private String uid;
    private String token;

    /**
     * 登陆成功的构造方法
     *
     * @param isSuccess
     * @param uid
     * @param token
     */
    public XPGLoginResultEvent(boolean isSuccess, String uid, String token) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

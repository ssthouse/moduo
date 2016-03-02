package com.ssthouse.moduo.model.event.xpg;

/**
 * 解绑设备的回调
 * Created by ssthouse on 2015/12/23.
 */
public class UnbindResultEvent {

    private boolean isSuccess;

    private String did;

    /**
     * 构造方法
     *
     * @param isSuccess
     * @param did
     */
    public UnbindResultEvent(boolean isSuccess, String did) {
        this.isSuccess = isSuccess;
        this.did = did;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}

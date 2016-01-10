package com.ssthouse.moduo.bean.event.scan;

/**
 * 扫描设备event
 * Created by ssthouse on 2016/1/10.
 */
public class ScanDeviceEvent {

    private boolean isSuccess;

    /**
     * 设备id
     */
    private String did;
    /**
     * 设备绑定密码
     */
    private String passCode;


    /**
     * 失败的event
     * @param isSuccess
     */
    public ScanDeviceEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * 成功的event
     * @param isSuccess
     * @param did
     * @param passCode
     */
    public ScanDeviceEvent(boolean isSuccess, String did, String passCode) {
        this.isSuccess = isSuccess;
        this.did = did;
        this.passCode = passCode;
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

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }
}

package com.ssthouse.moduo.model.bean;

/**
 * 一台魔哆的Info---对应于cloud上ModuoDevice table
 * Created by ssthouse on 2016/1/22.
 */
public class ModuoInfo {

    //机智云参数
    private String did;
    private String passCode;

    //视频参数
    private String cid;
    private String videoUsername;
    private String videoPassword;

    public ModuoInfo(String did, String passCode, String cid, String videoUsername, String videoPassword) {
        this.did = did;
        this.passCode = passCode;
        this.cid = cid;
        this.videoUsername = videoUsername;
        this.videoPassword = videoPassword;
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getVideoUsername() {
        return videoUsername;
    }

    public void setVideoUsername(String videoUsername) {
        this.videoUsername = videoUsername;
    }

    public String getVideoPassword() {
        return videoPassword;
    }

    public void setVideoPassword(String videoPassword) {
        this.videoPassword = videoPassword;
    }

    @Override
    public String toString() {
        return did + "   " + passCode + "    " + cid + "    " + videoUsername + "    " + videoPassword;
    }
}

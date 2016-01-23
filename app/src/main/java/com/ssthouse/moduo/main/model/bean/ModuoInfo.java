package com.ssthouse.moduo.main.model.bean;

/**
 * 一台魔哆的Info---对应于cloud上ModuoDevice table
 * Created by ssthouse on 2016/1/22.
 */
public class ModuoInfo {

    private String did;
    private String passcode;

    private String cid;
    private String videoUsername;
    private String videoPassword;

    public ModuoInfo(String did, String passcode, String cid, String videoUsername, String videoPassword) {
        this.did = did;
        this.passcode = passcode;
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

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
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
        return did + "   " + passcode + "    " + cid + "    " + videoUsername + "    " + videoPassword;
    }
}

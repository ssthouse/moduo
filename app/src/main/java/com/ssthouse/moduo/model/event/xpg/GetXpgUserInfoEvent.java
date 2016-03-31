package com.ssthouse.moduo.model.event.xpg;

import com.xtremeprog.xpgconnect.XPGUserInfo;

/**
 * 获取XpgUserInfo事件
 * Created by ssthouse on 16/3/31.
 */
public class GetXpgUserInfoEvent {

    private boolean success;

    private XPGUserInfo xpgUserInfo;

    public GetXpgUserInfoEvent(boolean success, XPGUserInfo xpgUserInfo) {
        this.success = success;
        this.xpgUserInfo = xpgUserInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public XPGUserInfo getXpgUserInfo() {
        return xpgUserInfo;
    }

    public void setXpgUserInfo(XPGUserInfo xpgUserInfo) {
        this.xpgUserInfo = xpgUserInfo;
    }
}

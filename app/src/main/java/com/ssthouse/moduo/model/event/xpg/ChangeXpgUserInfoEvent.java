package com.ssthouse.moduo.model.event.xpg;

/**
 * 改变XpgUserInfo结果回调
 * Created by ssthouse on 16/3/31.
 */
public class ChangeXpgUserInfoEvent {

    private boolean success;

    private String errorMessage;

    public ChangeXpgUserInfoEvent(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

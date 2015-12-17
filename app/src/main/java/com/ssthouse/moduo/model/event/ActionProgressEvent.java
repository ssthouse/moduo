package com.ssthouse.moduo.model.event;

/**
 * actionBar进度条
 * Created by ssthouse on 2015/12/17.
 */
public class ActionProgressEvent {

    private boolean isShow;

    public ActionProgressEvent(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}

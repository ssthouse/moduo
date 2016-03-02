package com.ssthouse.moduo.model.event.view;

import com.ssthouse.moduo.activity.SettingActivity;

/**
 * settingActivity状态变化event
 * Created by ssthouse on 2016/1/26.
 */
public class SettingAtyStateEvent {

    private SettingActivity.State toState;

    public SettingAtyStateEvent(SettingActivity.State toState) {
        this.toState = toState;
    }

    public SettingActivity.State getToState() {
        return toState;
    }

    public void setToState(SettingActivity.State toState) {
        this.toState = toState;
    }
}

package com.ssthouse.moduo.model.event.xpg;

/**
 * 登陆界面fragment切换事件
 * Created by ssthouse on 2015/12/19.
 */
public class RegisterFragmentChangeEvent {

    /**
     * 目标fragment
     */
    public enum NextFragment {
        LOGIN_FRAGMENT, PHONE_REGISTER_FRAGMENT, EMAIL_REGISTER_FRAGMENT
    }

    /**
     * 目标变化的fragment
     */
    private NextFragment nextFragment;

    /**
     * 构造方法
     *
     * @param nextFragment
     */
    public RegisterFragmentChangeEvent(NextFragment nextFragment) {
        this.nextFragment = nextFragment;
    }

    public NextFragment getNextFragment() {
        return nextFragment;
    }

    public void setNextFragment(NextFragment nextFragment) {
        this.nextFragment = nextFragment;
    }
}

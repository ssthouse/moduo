package com.ssthouse.moduo.activity.main;

/**
 * MainActivity的操作抽象
 * Created by ssthouse on 2016/2/15.
 */
public interface MainActivityView {

    void showWaitDialog(String msg);

    void dismissWaitDialog();

    void updateUI();

    //确认注销Dialog
    void showConfirmLogoutDialog();
    void dismissConfirmLogoutDialog();
}

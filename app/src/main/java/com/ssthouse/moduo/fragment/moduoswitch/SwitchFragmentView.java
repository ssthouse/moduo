package com.ssthouse.moduo.fragment.moduoswitch;

/**
 * View层操作抽象
 * Created by ssthouse on 2016/3/6.
 */
public interface SwitchFragmentView {

    //**********************dialog********************
    void showWaitDialog(String msg);

    void dismissWaitDialog();

    //操作选项Dialog
    void showOptionsDialog();
    void hideOptionsDialog();

    void showConfirmSwitchDialog();

    void dismissConfirmSwitchDialog();

    void showChangeRemarkDialog();

    void dismissChangeRemarkDialog();

    //************************fragment UI***************
    void showLoading();

    void showLoadErr();

    void showDeviceList();
}

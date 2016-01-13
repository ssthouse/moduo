package com.ssthouse.moduo.main.view;

/**
 * MainActivity的View层抽象
 * Created by ssthouse on 2016/1/10.
 */
public interface MainView {
    /**
     * 显示Dialog
     */
    void showDialog(String msg);

    /**
     * 隐藏Dialog
     */
    void dismissDialog();
}

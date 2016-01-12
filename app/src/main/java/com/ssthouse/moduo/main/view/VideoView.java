package com.ssthouse.moduo.main.view;

/**
 * VideoActivity界面操作的抽象
 * Created by ssthouse on 2016/1/12.
 */
public interface VideoView {

    /**
     * 显示正在calling fragment
     */
    void showCallingFragment();

    /**
     * 显示视频fragment
     */
    void showVideoFragment();

    /**
     * 显示Dialog
     */
    void showDialog(String msg);

    /**
     * 隐藏Dialog
     */
    void dismissDialog();
}

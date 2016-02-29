package com.ssthouse.moduo.fragment.video;

/**
 * 视频播放fragment控制接口
 * Created by ssthouse on 2016/2/1.
 */
public interface VideoFragmentView {

    //显示等待dialog
    void showWaitDialog(String msg);
    //隐藏等待Dialog
    void dismissWaitDialog();

    //显示确认退出Dialog
    void showConfirmDialog(String msg);

    //横屏
    void toLandscape();
    //竖屏
    void toPortrait();
}

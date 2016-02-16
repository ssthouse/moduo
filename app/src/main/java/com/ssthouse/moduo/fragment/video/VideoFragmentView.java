package com.ssthouse.moduo.fragment.video;

/**
 * 视频播放fragment控制接口
 * Created by ssthouse on 2016/2/1.
 */
public interface VideoFragmentView {

    //显示等待dialog
    void showDialog(String msg);
    //隐藏等待Dialog
    void dismissDialog();

    //显示确认退出Dialog
    void showConfirmDialog();

    //隐藏控制面板
    void hideCtrlPanel();

    //显示控制面板
    void showCtrlPanel();

    //横屏
    void toLandscape();
    //竖屏
    void toPortrait();

    //关闭视频
    void closeVideo();
}

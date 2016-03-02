package com.ssthouse.moduo.fragment.moduo.view.widget;

/**
 * AudioPlayButton对外公布的接口
 * Created by ssthouse on 2016/1/26.
 */
public interface IAudioPlayBtnControl {

    //播放动画
    void playAnim();

    //停止动画
    void stopAnim();

    //点击事件
    void onClick();

    //更新View
    void updateView();
}

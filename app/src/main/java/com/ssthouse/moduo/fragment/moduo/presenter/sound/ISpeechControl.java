package com.ssthouse.moduo.fragment.moduo.presenter.sound;

/**
 * 语音控制接口
 * Created by ssthouse on 2016/1/26.
 */
public interface ISpeechControl {

    //开启语音
    void startSpeech();

    //停止语义
    void stopSpeech();

    //取消语义
    void cancelSpeech();

    //获取当前录音文件路径
    String getCurrentFilePath();

    //获取音量大小
    int getVolumeLevel(int maxLevel);

    //当前录音文件是否存在
    boolean isCurrentAudioFileExit();

    //清除资源
    void destroy();
}

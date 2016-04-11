package com.ssthouse.moduo.fragment.moduo.presenter.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.ssthouse.moduo.fragment.moduo.view.widget.AudioPlayButton;

import java.io.IOException;

import timber.log.Timber;

/**
 * 单例:
 * 音频播放器
 * Created by ssthouse on 2016/1/25.
 */
public class AudioPlayer {

    private static AudioPlayer mInstance;

    private MediaPlayer mMediaPlayer;
    private Context mContext;

    //当前播放音乐文件路径
    private String currentFilePath = "";
    //当前控制的AudioButton
    private AudioPlayButton currentAudioBtn;
    //是否正在播放
    private boolean isPlaying;

    private AudioPlayer(Context context) {
        this.mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    public static AudioPlayer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AudioPlayer(context);
        }
        return mInstance;
    }

    //播放音频
    public void playAudio(String path, AudioPlayButton audioPlayButton) {
        //正在播放其他的音频---停止当前播放
        if (isPlaying && !currentFilePath.equals(path)) {
            mMediaPlayer.stop();
            currentAudioBtn.stopAnim();
        }
        currentFilePath = path;
        currentAudioBtn = audioPlayButton;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (currentAudioBtn != null) {
                        //停止动画
                        currentAudioBtn.stopAnim();
                    }
                }
            });
            mMediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            Timber.e("wrong!!!");
        }
    }

    public void pausePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }

    public void restartPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying() == false) {
            mMediaPlayer.start();
        }
    }

    //getter---------------------setter-------------------------------
    public static AudioPlayer getmInstance() {
        return mInstance;
    }

    public static void setmInstance(AudioPlayer mInstance) {
        AudioPlayer.mInstance = mInstance;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public void setCurrentFilePath(String currentFilePath) {
        this.currentFilePath = currentFilePath;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}

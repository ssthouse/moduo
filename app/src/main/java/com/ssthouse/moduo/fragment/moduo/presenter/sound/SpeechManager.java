package com.ssthouse.moduo.fragment.moduo.presenter.sound;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.fragment.moduo.model.event.SpeechUnderstandEvent;
import com.ssthouse.moduo.fragment.moduo.model.event.VolumeChangEvent;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;

import java.io.File;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 魔哆语音通话管理类:{
 *     语义理解
 *     声音文件保存
 * }
 * 发出的事件: {
 *     SpeechUnderstandEvent: 语义理解回调事件
 *     MsgBean: 理解出错的msgBean
 *     VolumeChangEvent: 音量变化事件, 用于更新对话的音量dialogUI
 * }
 * Created by ssthouse on 2016/1/26.
 */
public class SpeechManager implements ISpeechControl {

    private Context mContext;

    //录音文件的保存路径
    private String mDir = FileUtil.MODUO_TALK_PATH;
    private String mCurrentFilePath;

    //当前音量等级
    private int volumeLevel = 0;

    //语义理解器
    private SpeechUnderstander mSpeechUnderstander;

    private static SpeechManager mInstance;

    public static SpeechManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SpeechManager(context);
        }
        return mInstance;
    }

    private SpeechManager(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        //初始化---有监听器
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(mContext, mSpeechUdrInitListener);
        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "100000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "100000");
        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "1");
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");

        //参数设置为语义抽取
        mSpeechUnderstander.setParameter(SpeechConstant.DOMAIN, "iat");
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mSpeechUnderstander.setParameter(SpeechConstant.NLP_VERSION, "2.0");
    }

    @Override
    public void startSpeech() {
        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            //Timber.e("停止录音");
        }
        //设置音频输出路径
        mCurrentFilePath = generateFilePath();
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, mCurrentFilePath);
        //开始语音理解
        mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
    }

    @Override
    public void stopSpeech() {
        mSpeechUnderstander.stopUnderstanding();
        //Timber.e("停止语义理解");
    }

    @Override
    public void cancelSpeech() {
        mSpeechUnderstander.cancel();
        //Timber.e("取消语义理解");
    }

    @Override
    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    @Override
    public int getVolumeLevel(int maxLevel) {
        return (int) (1.0f * volumeLevel / 30 * maxLevel);
    }

    @Override
    public boolean isCurrentAudioFileExit() {
        File currentAudioFile = new File(mCurrentFilePath);
        Timber.e("mCurrentFilePath:\t" + mCurrentFilePath + (currentAudioFile.exists() ? "存在" : "不存在"));
        return currentAudioFile.exists();
    }

    // 退出时释放连接
    @Override
    public void destroy() {
        mSpeechUnderstander.cancel();
        mSpeechUnderstander.destroy();
    }

    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener mSpeechUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Timber.e("speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Timber.e("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result && !TextUtils.isEmpty(result.getResultString())) {
                Timber.e("识别结果:\t" + result.getResultString());
                EventBus.getDefault().post(new SpeechUnderstandEvent(true, result.getResultString()));
            } else {
                EventBus.getDefault().post(new SpeechUnderstandEvent(false, null));
                Timber.e("识别结果不正确。");
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            if (volumeLevel == volume) {
                return;
            }
            volumeLevel = volume;
            //抛出声音变化
            EventBus.getDefault().post(new VolumeChangEvent(volume));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            //Timber.e("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            //Timber.e("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            EventBus.getDefault().post(MsgBean.getInstance(MsgBean.TYPE_MODUO_TEXT, MsgBean.STATE_SENT, "您说什么? 我没听清。"));
            Timber.e("error" + error.getPlainDescription(true));
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }
    };

    //获取随机文件名
    private String generateFilePath() {
        return mDir + System.currentTimeMillis() + ".wav";
    }
}

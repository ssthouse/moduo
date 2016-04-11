package com.ssthouse.moduo.control.video;


import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.os.Build;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.codec.AudioType;

import timber.log.Timber;

/**
 * 众云sdk音频封装
 * Created by ssthouse on 2015/12/17.
 */
public class AudioHandler {

    /**
     * 主要管理类
     */
    private Media media;

    private long liveStreamId;// 实时音视频流
    private long revStreamId;// 逆向语音流
    private long decoderId;
    private int audioPlaySampleRate;
    /**
     * 是否在录制
     * 默认开始时对讲开启
     */
    private boolean isRecordAudio = true;

    /**
     * 播放线程
     */
    private Thread playThread;
    /**
     * 记录线程
     */
    private Thread recordThread;

    /**
     * 通道配置
     */
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    /**
     * 音频格式
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 构造方法
     *
     * @param sampleRateInHz 样本频率
     * @param channel        通道
     * @param streamId       流id
     * @param decoderId      解码器id
     * @param media          主要管理类
     * @param streamerCid    采集器cid
     */
    public AudioHandler(int sampleRateInHz, int channel, long streamId, long decoderId,
                        Media media, long streamerCid) {
        if (channel == 1) {
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        } else {
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        }
        this.audioPlaySampleRate = sampleRateInHz;
        this.media = media;
        this.liveStreamId = streamId;
        this.decoderId = decoderId;

        // 设置观看端对讲语音到采集端的格式 8000采样率 pcm16
        media.setRevAudioStreamProperty(new MediaDataDesc(AudioType.PCM16, 8000, 1, 16));
        revStreamId = media.startRevAudioStream(streamerCid);//根据采集端cid打开语音输入流
    }

    // 开始发送语音到采集端
    public void startTalk() {
        isRecordAudio = true;
    }

    // 停止发送语音到采集端
    public void stopTalk() {
        isRecordAudio = false;
    }

    /**
     * 创建记录器
     *
     * @return
     */
    private AudioRecord createAudioRecord() {
        AudioRecord audioRecord;
        int minBufSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        // 设置最小缓存大小
        if (minBufSize != AudioRecord.ERROR_BAD_VALUE) {
            if (minBufSize % 4096 != 0) {
                minBufSize = (minBufSize / 4096 + 1) * 4096;
            }
        }
        // 创建AudioRecord对象
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize);
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            initAec(audioRecord);
            return audioRecord;
        } else {
            return null;
        }
    }

    //调用回声消除
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initAec(AudioRecord audioRecord) {
        Timber.e(AcousticEchoCanceler.isAvailable() ? "回声消除可用" : "回声消除不可用");
        if (!AcousticEchoCanceler.isAvailable()) {
            return;
        }
        //初始化回应消除
        int sessionId = audioRecord.getAudioSessionId();
        AcousticEchoCanceler aec = AcousticEchoCanceler.create(sessionId);
        aec.setControlStatusListener(new AudioEffect.OnControlStatusChangeListener() {
            @Override
            public void onControlStatusChange(AudioEffect effect, boolean controlGranted) {
                Timber.e("has Control:\t" + controlGranted);
            }
        });
        aec.setEnableStatusListener(new AudioEffect.OnEnableStatusChangeListener() {
            @Override
            public void onEnableStatusChange(AudioEffect effect, boolean enabled) {
                Timber.e("aec enable:\t" + enabled);
            }
        });
        aec.setEnabled(true);
        Timber.e("set aec enable:\t" + aec.getEnabled());
    }

    /**
     * 创建tracker
     *
     * @return
     */
    private AudioTrack createTracker() {
        AudioTrack tracker;
        int maxJitter = AudioTrack.getMinBufferSize(audioPlaySampleRate, channelConfig, audioFormat);
        tracker = new AudioTrack(AudioManager.STREAM_MUSIC,
                audioPlaySampleRate, channelConfig, audioFormat, maxJitter, AudioTrack.MODE_STREAM);
        if (tracker.getState() == AudioTrack.STATE_INITIALIZED) {
            return tracker;
        } else {
            return null;
        }
    }

    /**
     * 释放资源
     */
    public void releaseAudio() {
        if (playThread != null && playThread.isAlive()) {
            playThread.interrupt();
            playThread = null;
        }
        if (recordThread != null && recordThread.isAlive()) {
            recordThread.interrupt();
            recordThread = null;
        }
//        if (media != null) {
//            media.stopRevAudioStream(revStreamId);
//        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 开始工作
     */
    public void startAudioWorking() {
        //播放线程开始工作
        if (playThread == null) {
            playThread = new Thread(new Runnable() {
                public void run() {
                    AudioTrack audioPlay = createTracker();
                    if (audioPlay == null) {
                        Timber.e("create audio play return null");
                        return;
                    }
                    audioPlay.play();
                    try {
                        byte[] mAudioPlayData = new byte[4096];
                        int size = 0;
                        while (true) {
                            // 读取解码后的音频数据
                            size = media.getAudioDecodedData(liveStreamId, decoderId, mAudioPlayData);
                            if (size > 0) {
                                audioPlay.write(mAudioPlayData, 0, size);
                            } else {
                                Thread.sleep(10);
                            }
                        }
                    } catch (InterruptedException e) {
                        Timber.e("e:" + e.toString());
                    } finally {
                        audioPlay.release();
                        audioPlay = null;
                    }

                }
            });
            playThread.start();
        }
        //记录线程开始工作
        if (recordThread == null) {
            recordThread = new Thread(new Runnable() {

                public void run() {
                    AudioRecord audioRecord = createAudioRecord();
                    if (audioRecord == null) {
                        Timber.e("create audio record return null");
                        return;
                    }
                    try {
                        short[] audioRecordData = new short[320];
                        int readsize = 0;
                        while (true) {
                            if (isRecordAudio) {
                                audioRecord.startRecording();
                                readsize = audioRecord.read(audioRecordData, 0, 320);
                                if (readsize > 0) {
                                    media.writeRevAudioStreamData(audioRecordData, readsize);// 发送对讲语音到采集端，sdk内部进行g711编码
                                }
                            } else {
                                if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                                    audioRecord.stop();
                                }
                            }
                            Thread.sleep(1);
                        }
                    } catch (InterruptedException | RuntimeException e) {
                        Timber.e(e.toString());
                    } finally {
                        isRecordAudio = false;
                        if (audioRecord != null) {
                            audioRecord.release();
                            audioRecord = null;
                        }
                        if (media != null) {
                            media.stopRevAudioStream(revStreamId);
                        }
                        Timber.e("release audio record");
                    }
                }
            });
            recordThread.start();
        }
    }
}

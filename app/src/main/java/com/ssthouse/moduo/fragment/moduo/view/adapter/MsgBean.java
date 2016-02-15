package com.ssthouse.moduo.fragment.moduo.view.adapter;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 消息数据bean
 * Created by ssthouse on 2016/1/25.
 */
@Table(name = MsgBean.TABLE_MSG)
public class MsgBean extends Model{

    //映射到数据库
    public static final String TABLE_MSG = "Message";
    //msg类型
    public static final String COLUMN_MSG_TYPE = "MsgType";
    //msg状态
    public static final String COLUMN_MSG_STATE = "MsgState";
    //文字
    public static final String COLUMN_TEXT = "Text";
    //音频
    public static final String COLUMN_AUDIO_PATH = "AudioPath";
    public static final String COLUMN_AUDIO_DURATION = "AudioDuration";
    //图片
    public static final String COLUMN_IMAGE_PATH = "ImgPath";
    //方向
    public static final String COLUMN_IS_FROM_MODUO = "IsModuo";
    //发出时间
    public static final String COLUMN_TIME_STAMP = "TimeStamp";

    //消息类型枚
    public static final int TYPE_MODUO_TEXT = 1000;
    public static final int TYPE_MODUO_AUDIO = 1001;
    public static final int TYPE_MODUO_IMAGE = 1002;
    public static final int TYPE_USER_TEXT = 1003;
    public static final int TYPE_USER_AUDIO = 1004;
    public static final int TYPE_USER_IMAGE = 1005;

    //消息状态
    public static final int STATE_FILED = 1006;
    public static final int STATE_SENT = 1007;
    public static final int STATE_SENDING = 1008;
    public static final int STATE_RECEIPT = 1009;

    //消息类型
    @Column(name = COLUMN_MSG_TYPE)
    private int msgType;
    //消息状态
    @Column(name = COLUMN_MSG_STATE)
    private int msgState;

    //文字
    @Column(name = COLUMN_TEXT)
    private String text;
    //音频数据
    @Column(name = COLUMN_AUDIO_PATH)
    private String audioFilePath;
    @Column(name = COLUMN_AUDIO_DURATION)
    private int audioDuration;
    //图片数据
    @Column(name = COLUMN_IMAGE_PATH)
    private String imgFilePath;

    //是否是魔哆发送的
    @Column(name = COLUMN_IS_FROM_MODUO)
    private boolean isFromModuo;
    @Column(name = COLUMN_TIME_STAMP)
    private long timeStamp;

    //空 构造方法
    public MsgBean() {
        super();
    }

    /**
     * 获取一个实例
     */
    public static MsgBean getInstance(int msgType, int msgState, String content) {
        //新建msg---初始化 类型---状态---时间
        MsgBean bean = new MsgBean();
        bean.setMsgType(msgType);
        bean.setMsgState(msgState);
        bean.setTimeStamp(System.currentTimeMillis());
        switch (msgType) {
            case TYPE_MODUO_TEXT:
                bean.setFromModuo(true);
                bean.setText(content);
                break;
            case TYPE_MODUO_AUDIO:
                bean.setFromModuo(true);
                bean.setAudioFilePath(content);
                break;
            case TYPE_MODUO_IMAGE:
                bean.setFromModuo(true);
                bean.setImgFilePath(content);
                break;
            case TYPE_USER_TEXT:
                bean.setFromModuo(false);
                bean.setText(content);
                break;
            case TYPE_USER_AUDIO:
                bean.setFromModuo(false);
                bean.setAudioFilePath(content);
                break;
            case TYPE_USER_IMAGE:
                bean.setFromModuo(false);
                bean.setImgFilePath(content);
                break;
        }
        return bean;
    }

    //getter-------------------setter-----------------------------------------------------

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgState() {
        return msgState;
    }

    public void setMsgState(int msgState) {
        this.msgState = msgState;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    public void setImgFilePath(String imgFilePath) {
        this.imgFilePath = imgFilePath;
    }

    public boolean isFromModuo() {
        return isFromModuo;
    }

    public void setFromModuo(boolean fromModuo) {
        isFromModuo = fromModuo;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(int audioDuration) {
        this.audioDuration = audioDuration;
    }
}

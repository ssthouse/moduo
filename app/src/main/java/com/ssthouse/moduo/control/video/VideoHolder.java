package com.ssthouse.moduo.control.video;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.ichano.rvs.viewer.render.GLViewYuvRender;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.model.event.video.VideoExceptionEvent;
import com.ssthouse.moduo.model.event.video.VideoReadyEvent;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 众云sdk的视频管理器{
 *     控制视频的初始化, 开启, 和关闭
 * }
 * Created by ssthouse on 2016/1/15.
 */
public class VideoHolder {

    /**
     * SDK控制类
     */
    private Viewer viewer;
    private Media media;

    private Context context;

    /**
     * 视频承接控件
     */
    private RelativeLayout surfaceViewLayout;

    private long liveStreamDid;//播放流id
    private long decoderId; //解码器id
    private long streamerCid;// 要观看的采集端cid

    /**
     * 视频播放控件
     */
    private GLSurfaceView glSurfaceView;
    private GLViewYuvRender myRenderer;
    //当前帧的yuv数据
    private byte[] y, u, v;
    private int videoWidth, videoHeight;

    /**
     * 播放控制类
     */
    private AudioHandler audioHandler;

    /**
     * 构造方法
     *
     * @param surfaceViewLayout
     * @param streamerCid
     */
    public VideoHolder(Context context, RelativeLayout surfaceViewLayout, long streamerCid) {
        this.context = context;
        this.surfaceViewLayout = surfaceViewLayout;
        this.streamerCid = streamerCid;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        viewer = Viewer.getViewer();
        media = viewer.getMedia();

        //初始化播放控件
        glSurfaceView = new GLSurfaceView(context);
        glSurfaceView.setEGLContextClientVersion(2);
        myRenderer = new GLViewYuvRender();
        glSurfaceView.setRenderer(myRenderer);
    }

    /**
     * 解码回调器
     */
    private GLViewYuvRender.RenderYUVFrame renderYUVFrame = new GLViewYuvRender.RenderYUVFrame() {
        @Override
        public void onRenderData(byte[] bytes, byte[] bytes1, byte[] bytes2) {
            y = bytes;
            u = bytes1;
            v = bytes2;
            media.getVideoDecodedData(liveStreamDid, decoderId, bytes, bytes1, bytes2);
        }
    };

    /**
     * 媒体流的回调
     */
    private MediaStreamStateCallback mediaStreamStateCallback = new MediaStreamStateCallback() {
        @Override
        public void onMediaStreamState(long streamId, MediaStreamState mediaStreamState) {
            Timber.e("streamId :" + streamId + ",state:" + mediaStreamState.intValue());
            //监测链接状态
            if (mediaStreamState == MediaStreamState.CREATED) {
                //视频准备完毕事件
                EventBus.getDefault().post(new VideoReadyEvent(true));
                MediaDataDesc desc = media.getStreamDesc(liveStreamDid);
                //判断数据是否有效
                if (desc == null || desc.getSampRate() == 0 || desc.getVideoWidth() <= 0 || desc.getVideoHeight() <= 0) {
                    Timber.e("get media desc error!");
                    EventBus.getDefault().post(new VideoExceptionEvent());
                    return;
                }
                Timber.e("video :" + desc.getVideoType().toString() + ","
                        + desc.getVideoWidth() + "," + desc.getVideoHeight());
                Timber.e("audio :" + desc.getAudioType().toString() + ","
                        + desc.getSampRate());
                // 根据对端的音视频格式进行编码器初始化，不使用sdk内置h264解码器可以不用关心
                decoderId = media.initAVDecoder(desc.getAudioType(), desc.getSampRate());
                videoWidth = desc.getVideoWidth();
                videoHeight = desc.getVideoHeight();
                myRenderer.setVideoDimension(videoWidth, videoHeight);
                myRenderer.setYuvDataRender(renderYUVFrame);
//                if (desc.getAudioType() != AudioType.INVALID) {
                audioHandler = new AudioHandler(desc.getSampRate(), desc.getChannel(), liveStreamDid, decoderId, media, streamerCid);
                audioHandler.startAudioWorking();
//                }
            } else {
                stopWatch();
                //视频准备完毕事件
                EventBus.getDefault().post(new VideoReadyEvent(false));
            }
        }
    };

    //开始实时视频
    public void startLiveVideo() {
        surfaceViewLayout.addView(glSurfaceView);
        liveStreamDid = media.openLiveStream(streamerCid, 0, 0, 0);// 测试打开实时视频流
        Timber.e("liveStreamDid :" + liveStreamDid + "   cid" + streamerCid);
    }

    //停止音视频观看
    public void stopWatch() {
        if (audioHandler != null) {
            audioHandler.releaseAudio();// stop audio play and record
            audioHandler = null;
        }
        surfaceViewLayout.removeView(glSurfaceView);// stop video render
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (decoderId != 0) {
            media.destoryAVDecoder(decoderId);// 销毁解码器
            decoderId = 0;
        }
        if (liveStreamDid != 0) {
            media.closeStream(liveStreamDid);// 关闭实时流
            liveStreamDid = 0;
        }
    }

    //开启对讲
    public boolean startTalk() {
        if (audioHandler == null) {
            return false;
        } else {
            audioHandler.startTalk();
            return true;
        }
    }

    //停止对讲
    public boolean stopTalk() {
        if (audioHandler == null) {
            return false;
        } else {
            audioHandler.stopTalk();
            return true;
        }
    }

    //测试自定义命令
    public void sendCmd(View view) {
        boolean ret = viewer.getCommand().sendCustomData(streamerCid, "test".getBytes());
        Timber.e("send cmd ret:" + ret);
    }

    //截取一帧的图片
    public void saveOneFrameJpeg() {
        if (videoWidth == 0 || videoHeight == 0) {
            Toast.show("视频数据格式有误, 请稍候重试");
            return;
        }
        int yuvi = videoWidth * videoHeight;
        int uvi = 0;
        byte[] yuv = new byte[videoWidth * videoHeight * 3 / 2];
        System.arraycopy(y, 0, yuv, 0, yuvi);
        for (int i = 0; i < videoHeight / 2; i++) {
            for (int j = 0; j < videoWidth / 2; j++) {
                yuv[yuvi++] = v[uvi];
                yuv[yuvi++] = u[uvi++];
            }
        }
        String filePath = FileUtil.generateNewPicFilePath();
        YuvImage yuvImage = new YuvImage(yuv, ImageFormat.NV21, videoWidth, videoHeight, null);
        try {
            yuvImage.compressToJpeg(new Rect(0, 0, videoWidth, videoHeight), 100, new FileOutputStream(filePath));
            Toast.show("截屏成功保存至:\tSD卡根目录\\" + filePath);
        } catch (FileNotFoundException e) {
            Toast.show("图片截取失败!");
            e.printStackTrace();
        }
    }

    public void resume() {
        media.setMediaStreamStateCallback(mediaStreamStateCallback);
    }

    public void stop() {
        media.setMediaStreamStateCallback(null);
        stopWatch();
    }

    //getter----------------------------

    public long getLiveStreamDid() {
        return liveStreamDid;
    }

    public long getDecoderId() {
        return decoderId;
    }

    public long getStreamerCid() {
        return streamerCid;
    }
}

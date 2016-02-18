package com.ssthouse.moduo.control.video;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.ichano.rvs.viewer.render.GLViewYuvRender;
import com.ssthouse.moduo.model.event.video.VideoReadyEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 视频管理类---减轻videoFragment
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

    private long liveStreamId;//播放流id
    private long decoderId; //解码器id
    private long streamerCid;// 要观看的采集端cid

    /**
     * 视频播放控件
     */
    private GLSurfaceView glSurfaceView;
    private GLViewYuvRender myRenderer;

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
            media.getVideoDecodedData(liveStreamId, decoderId, bytes, bytes1, bytes2);
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
                MediaDataDesc desc = media.getStreamDesc(liveStreamId);
                if (desc == null) {
                    Timber.e("get media desc error!");
                    return;
                }
                Timber.e("video :" + desc.getVideoType().toString() + ","
                        + desc.getVideoWidth() + "," + desc.getVideoHeight());
                Timber.e("audio :" + desc.getAudioType().toString() + ","
                        + desc.getSampRate());
                // 根据对端的音视频格式进行编码器初始化，不使用sdk内置h264解码器可以不用关心
                decoderId = media.initAVDecoder(desc.getAudioType(), desc.getSampRate());
                myRenderer.setVideoDimension(desc.getVideoWidth(), desc.getVideoHeight());
                myRenderer.setYuvDataRender(renderYUVFrame);
//                if (desc.getAudioType() != AudioType.INVALID) {
                    audioHandler = new AudioHandler(desc.getSampRate(), desc.getChannel(), liveStreamId, decoderId, media, streamerCid);
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
        liveStreamId = media.openLiveStream(streamerCid, 0, 0, 0);// 测试打开实时视频流
        Timber.e("liveStreamId :" + liveStreamId + "   cid" + streamerCid);
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
        if (liveStreamId != 0) {
            media.closeStream(liveStreamId);// 关闭实时流
            liveStreamId = 0;
        }
    }

    //开启对讲
    public boolean startTalk(){
        if(audioHandler == null){
            return false;
        }else {
            audioHandler.startTalk();
            return true;
        }
    }

    //停止对讲
    public boolean stopTalk(){
        if(audioHandler == null){
            return false;
        }else{
            audioHandler.stopTalk();
            return true;
        }
    }

    //测试自定义命令
    public void sendCmd(View view) {
        boolean ret = viewer.getCommand().sendCustomData(streamerCid, "test".getBytes());
        Timber.e("send cmd ret:" + ret);
    }

    public void resume() {
        media.setMediaStreamStateCallback(mediaStreamStateCallback);
    }

    public void stop() {
        media.setMediaStreamStateCallback(null);
        stopWatch();
    }
}

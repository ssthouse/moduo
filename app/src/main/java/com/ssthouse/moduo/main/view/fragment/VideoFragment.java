package com.ssthouse.moduo.main.view.fragment;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.codec.AudioType;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.ichano.rvs.viewer.render.GLViewYuvRender;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.video.AudioHandler;
import com.ssthouse.moduo.main.view.VideoActivity;

import timber.log.Timber;

/**
 * 视频通话fragment
 * Created by ssthouse on 2016/1/12.
 */
public class VideoFragment extends Fragment {

    private MaterialDialog waitDialog;

    /**
     * SDK控制类
     */
    private Viewer viewer;
    private Media media;

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
     * 获取fragment实例
     *
     * @param cidNumber
     * @return
     */
    public static VideoFragment newInstance(long cidNumber) {
        VideoFragment videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(VideoActivity.ARGUMENT_CID_NUMBER, cidNumber);
        videoFragment.setArguments(bundle);
        return videoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化数据
        streamerCid = getArguments().getLong(VideoActivity.ARGUMENT_CID_NUMBER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //初始化Video
        initVideo();

        //初始化view
        View rootView = inflater.inflate(R.layout.fragment_video_display, container, false);
        initView(rootView);
        return rootView;
    }

    private void initVideo() {
        viewer = Viewer.getViewer();
        media = viewer.getMedia();
    }

    private void initView(View rootView) {
        surfaceViewLayout = (RelativeLayout) rootView.findViewById(R.id.id_rl_container);

        //初始化播放控件
        glSurfaceView = new GLSurfaceView(getContext());
        glSurfaceView.setEGLContextClientVersion(2);
        myRenderer = new GLViewYuvRender();
        glSurfaceView.setRenderer(myRenderer);

        waitDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .autoDismiss(false)
                .build();
    }

    /**
     * 解码器回调器
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
            //隐藏dialog
            waitDialog.dismiss();
            //监测链接状态
            if (mediaStreamState == MediaStreamState.CREATED) {
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
                decoderId = media.initAVDecoder(desc.getAudioType(),
                        desc.getSampRate());
                myRenderer.setVideoDimension(desc.getVideoWidth(),
                        desc.getVideoHeight());
                myRenderer.setYuvDataRender(renderYUVFrame);
                if (desc.getAudioType() != AudioType.INVALID) {
                    audioHandler = new AudioHandler(desc.getSampRate(),
                            desc.getChannel(), liveStreamId, decoderId, media, streamerCid);
                    audioHandler.startAudioWorking();
                }
            } else {
                stopWatch();
            }
        }
    };

    //开始实时视频
    private void startLiveVideo() {
        surfaceViewLayout.addView(glSurfaceView);
        liveStreamId = media.openLiveStream(streamerCid, 0, 0, 0);// 测试打开实时视频流
        Timber.e("liveStreamId :" + liveStreamId);
        //初始显示等待dialog
        waitDialog.show();
    }

    //停止音视频观看
    private void stopWatch() {
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

    //测试自定义命令
    public void sendCmd(View view) {
        boolean ret = viewer.getCommand().sendCustomData(streamerCid, "test".getBytes());
        Timber.e("send cmd ret:" + ret);
    }

    /*
    生命周期控制
     */
    @Override
    public void onStart() {
        super.onStart();
        startLiveVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        media.setMediaStreamStateCallback(mediaStreamStateCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        media.setMediaStreamStateCallback(null);
        stopWatch();
    }
}

package com.ssthouse.moduo.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.MediaDataDesc;
import com.ichano.rvs.viewer.callback.MediaStreamStateCallback;
import com.ichano.rvs.viewer.codec.AudioType;
import com.ichano.rvs.viewer.constant.MediaStreamState;
import com.ichano.rvs.viewer.render.GLViewYuvRender;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.video.AudioHandler;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 视频对话activity
 * Created by ssthouse on 2015/12/17.
 */
public class VideoActivity extends Activity {
    /**
     * SDK控制类
     */
    private Viewer viewer;
    private Media media;

    @Bind(R.id.id_rl_container)
    RelativeLayout surfaceViewLayout;

    @Bind(R.id.id_view_toggle)
    CompoundButton btnToggle;

    //等待dialog
    private MaterialDialog waitDialog;

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
     * 启动当前activityu
     *
     * @param context 上下文
     * @param cid     采集端cid
     */
    public static void start(Context context, long cid) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("cid", cid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        initView();

        viewer = Viewer.getViewer();
        media = viewer.getMedia();

        Intent it = getIntent();
        streamerCid = it.getLongExtra("cid", 0);
    }

    private void initView() {
        //初始化播放控件
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        myRenderer = new GLViewYuvRender();
        glSurfaceView.setRenderer(myRenderer);

        //播放控制
        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (audioHandler != null) {
                        audioHandler.startTalk();
                    }
                } else {
                    if (audioHandler != null) {
                        audioHandler.stopTalk();
                    }
                }
            }
        });

        //等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 显示等待视频Dialog
     *
     * @param msg
     */
    private void showWaitDialog(String msg) {
        View customView = waitDialog.getCustomView();
        TextView tvWait = (TextView) customView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    /**
     * TODO
     */
    public void onEventMainThread(SessionStateEvent event) {

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
            //收到回调的时候隐藏dialog
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

    @Override
    protected void onStart() {
        super.onStart();
        startLiveVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Timber.e("我开启了streamer回调");
        media.setMediaStreamStateCallback(mediaStreamStateCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        media.setMediaStreamStateCallback(null);
        stopWatch();
    }

    //开始实时视频
    private void startLiveVideo() {
        surfaceViewLayout.addView(glSurfaceView);
        liveStreamId = media.openLiveStream(streamerCid, 0, 0, 0);// 测试打开实时视频流
        Timber.e("liveStreamId :" + liveStreamId);
        //初始显示等待dialog
        showWaitDialog("正在加载视频,请稍候");
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
}

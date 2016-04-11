package com.ssthouse.moduo.control.video;

import android.content.Context;
import android.os.Environment;

import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.StreamerStateListener;
import com.ichano.rvs.viewer.callback.ViewerCallback;
import com.ichano.rvs.viewer.constant.LoginError;
import com.ichano.rvs.viewer.constant.LoginState;
import com.ichano.rvs.viewer.constant.RvsSessionState;
import com.ichano.rvs.viewer.constant.StreamerConfigState;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ssthouse.moduo.model.bean.device.Device;
import com.ssthouse.moduo.model.cons.Constant;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;
import com.ssthouse.moduo.model.event.video.StreamerConfigChangedEvent;
import com.ssthouse.moduo.model.event.video.StreamerConnectChangedEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 单例
 * 众云sdk连接状态控制{
 *     发出事件: SessionStateEvent  对话状态变化事件
 *     StreamerConnectChangedEvent, StreamerConfigChangedEvent(这两个事件并没有接收器,
 *     主要是在控制台打印输出当前sdk连接状态)
 * }
 * Created by ssthouse on 2015/12/17.
 */
public class Communication {

    /**
     * 单例
     */
    private static Communication mInstance;

    private Context mContext;

    /**
     * 视频SDK总管理类
     */
    private Viewer mViewer;

    //视频SDK登陆状态
    private static boolean login;
    //当前采集端的状态   初始状态为初始化
    private StreamerPresenceState streamerPresenceState = StreamerPresenceState.INIT;

    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static Communication getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Communication(context);
        }
        return mInstance;
    }

    /**
     * 加载视频对话sdk
     */
    private void loadSdkLib() {
        //ToastHelper.show(this, "加载视频sdk so 文件");
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("avdecoder");
        System.loadLibrary("sdk30");
        System.loadLibrary("viewer30");
    }

    /**
     * 构造方法
     *
     * @param context
     */
    private Communication(Context context) {
        //首先要加载sdk
        loadSdkLib();
        this.mContext = context;
        this.mViewer = Viewer.getViewer();
        init();
    }

    /**
     * 该类的初始化工作
     */
    private void init() {
        //初始化SDK
        mViewer.init(mContext, Constant.APP_VERSION_STR, mContext.getFilesDir().getAbsolutePath(),
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + Constant.EXTERNAL_VIDEO_FOLDER_NAME);
        //TODO---打印日志
        mViewer.setDebugEnable(false);
        //初始化注册认证信息
        mViewer.setLoginInfo(Constant.VideoSdkCons.companyID, Constant.VideoSdkCons.companyKey,
                Constant.VideoSdkCons.appID, Constant.VideoSdkCons.license);
        //设置回调
        mViewer.setCallBack(viewerCallback);
        //设置采集端状态回调
        mViewer.setStreamerStateListener(streamerStateListener);
        //正式登陆
        mViewer.login();
    }

    /**
     * viewer回调
     */
    private ViewerCallback viewerCallback = new ViewerCallback() {
        @Override
        public void onLoginResult(LoginState loginState, int i, LoginError loginError) {
            if (loginState == LoginState.CONNECTED) {
                //更新登陆状态
                setLogin(true);
            } else {
                setLogin(false);
            }
//            Timber.e("VIDEO SDK状态:\t" + loginState.name() + "\terror:   " + loginError.toString());
        }

        @Override
        public void onUpdateCID(long l) {
            //观看端cid发生变化
//            Timber.e("我的观看端cid发生了变化:\t" + l);
        }

        @Override
        public void onSessionStateChange(long l, RvsSessionState rvsSessionState) {
            //video会话状态变化事件
            EventBus.getDefault().post(new SessionStateEvent(rvsSessionState));
//            Timber.e("VIDEO SDK 会话状态为:\t" + rvsSessionState.name());
        }
    };

    /**
     * 采集端状态回调
     */
    private StreamerStateListener streamerStateListener = new StreamerStateListener() {
        @Override
        public void onStreamerPresenceState(long l, StreamerPresenceState streamerPresenceState) {
            EventBus.getDefault().post(new StreamerConnectChangedEvent(l, streamerPresenceState));
            //更新streamer状态
            Communication.this.streamerPresenceState = streamerPresenceState;
            Timber.e("streamer状态:\t" + streamerPresenceState.name());
        }

        @Override
        public void onStreamerConfigState(long l, StreamerConfigState streamerConfigState) {
            //参数配置变化事件
            EventBus.getDefault().post(new StreamerConfigChangedEvent(streamerConfigState));
            Timber.e("streamer配置状态:\t" + streamerConfigState.name());
        }
    };

    //添加采集端
    public void addStreamer(long streamerCid, String user, String pass) {
        //连接采集端
        mViewer.connectStreamer(streamerCid, user, pass);
        //获取采集端的信息
        mViewer.getStreamerInfoMgr().getStreamerInfo(streamerCid);
    }

    //添加采集端
    public void addStreamer(Device device) {
        if (device == null) {
            Timber.e("device is null");
            return;
        }
        mViewer.connectStreamer(Long.parseLong(device.getVideoCidNumber()),
                device.getVideoUsername(), device.getVideoPassword());
        //获取采集端的信息
        mViewer.getStreamerInfoMgr().getStreamerInfo(Long.parseLong(device.getVideoCidNumber()));
    }


    //删除采集端
    public void removeStreamer(long streamerCid) {
        mViewer.disconnectStreamer(streamerCid);
    }

    public void destroy() {
        mViewer.logout();//登出平台
        mViewer.destroy();//销毁sdk
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean isLogin) {
        login = isLogin;
    }

    public StreamerPresenceState getStreamerPresenceState() {
        return streamerPresenceState;
    }

    public void setStreamerPresenceState(StreamerPresenceState streamerPresenceState) {
        this.streamerPresenceState = streamerPresenceState;
    }

    public Viewer getmViewer() {
        return mViewer;
    }
}

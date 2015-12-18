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
import com.orhanobut.logger.Logger;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.model.Constant;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;
import com.ssthouse.moduo.model.event.video.StreamerConfigChangedEvent;
import com.ssthouse.moduo.model.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;

import de.greenrobot.event.EventBus;

/**
 * 掌控视频对讲的类
 * 登陆, 连接, 销毁 等操作
 * 使用eventbus进行通信
 * Created by ssthouse on 2015/12/17.
 */
public class Communication {

    private static Communication instance;

    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static Communication getInstance(Context context) {
        if (instance == null) {
            instance = new Communication(context);
        }
        return instance;
    }

    private Context context;

    private Viewer viewer;

    /**
     * 登陆状态
     */
    private boolean hasLogin;

    /**
     * viewer回调
     */
    private ViewerCallback viewerCallback = new ViewerCallback() {
        @Override
        public void onLoginResult(LoginState loginState, int i, LoginError loginError) {
            //TODO---需呀判断登陆失败
            if (loginState == LoginState.CONNECTED) {
                //更新登陆状态
                hasLogin = true;
                //放出登陆成功消息
                EventBus.getDefault().post(new ViewerLoginResultEvent(true));
            }
            Logger.e("现在状态是:\t" + loginState.name());
        }

        @Override
        public void onUpdateCID(long l) {
            //TODO---观看端cid发生变化
            Logger.e("我的观看端cid发生了变化:\t" + l);
        }

        @Override
        public void onSessionStateChange(long l, RvsSessionState rvsSessionState) {
            //TODO---抛出video回话状态变化事件
            EventBus.getDefault().post(new SessionStateEvent(rvsSessionState));
            Logger.e(rvsSessionState.name());
        }
    };

    /**
     * 采集端状态回调
     */
    private StreamerStateListener streamerStateListener = new StreamerStateListener() {
        @Override
        public void onStreamerPresenceState(long l, StreamerPresenceState streamerPresenceState) {
            //TODO
            EventBus.getDefault().post(new StreamerConnectChangedEvent(streamerPresenceState));
            Logger.e("目前streamer的链接状态是:\t" + streamerPresenceState.name());
            ToastHelper.show(context, "目前streamer的链接状态是:\t" + streamerPresenceState.name());
        }

        @Override
        public void onStreamerConfigState(long l, StreamerConfigState streamerConfigState) {
            //TODO
            EventBus.getDefault().post(new StreamerConfigChangedEvent(streamerConfigState));
            Logger.e("目前streamer的配置状态是:\t" + streamerConfigState.name());
            ToastHelper.show(context,"目前streamer的配置状态是:\t" + streamerConfigState.name());
        }
    };

    /**
     * 构造方法
     *
     * @param context
     */
    private Communication(Context context) {
        this.context = context;
        this.viewer = Viewer.getViewer();
        init();
    }

    /**
     * 该类的初始化工作
     */
    private void init() {
        //初始化SDK
        viewer.init(context, Constant.APP_VERSION_STR, context.getFilesDir().getAbsolutePath(),
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Constant.EXTERNAL_VIDEO_FOLDER_NAME);
        //TODO---打印日志
        viewer.setDebugEnable(Constant.isDebug);
        //初始化注册认证信息
        viewer.setLoginInfo(Constant.VideoCons.companyID, Constant.VideoCons.companyKey,
                Constant.VideoCons.appID, Constant.VideoCons.license);
        //设置回调
        viewer.setCallBack(viewerCallback);
        //设置采集端状态回调
        viewer.setStreamerStateListener(streamerStateListener);
        //正式登陆
        viewer.login();
    }

    //添加采集端
    public void addStreamer(long streamerCid, String user, String pass) {
        //连接采集端
        viewer.connectStreamer(streamerCid, user, pass);
        //获取采集端的信息
        viewer.getStreamerInfoMgr().getStreamerInfo(streamerCid);
    }

    //删除采集端
    public void removeStreamer(long streamerCid) {
        viewer.disconnectStreamer(streamerCid);
    }

    public void destory() {
        viewer.logout();//登出平台
        viewer.destroy();//销毁sdk
    }

    public boolean isHasLogin() {
        return hasLogin;
    }
}

package com.ssthouse.moduo.control.video;

import android.content.Context;

import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.StreamerStateListener;
import com.ichano.rvs.viewer.callback.ViewerCallback;
import com.ichano.rvs.viewer.constant.LoginError;
import com.ichano.rvs.viewer.constant.LoginState;
import com.ichano.rvs.viewer.constant.RvsSessionState;
import com.ichano.rvs.viewer.constant.StreamerConfigState;
import com.ichano.rvs.viewer.constant.StreamerPresenceState;
import com.ssthouse.moduo.model.Constant;
import com.ssthouse.moduo.model.event.SessionStateEvent;
import com.ssthouse.moduo.model.event.StreamerConfigChangedEvent;
import com.ssthouse.moduo.model.event.StreamerConnectChangedEvent;
import com.ssthouse.moduo.model.event.ViewerLoginResultEvent;

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
     * @param context
     * @return
     */
    public static Communication getInstance(Context context){
        if(instance == null){
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
            if(loginState == LoginState.CONNECTED){
                //更新登陆状态
                hasLogin = true;
                //放出登陆成功消息
                EventBus.getDefault().post(new ViewerLoginResultEvent(true));
            }
        }

        @Override
        public void onUpdateCID(long l) {

        }

        @Override
        public void onSessionStateChange(long l, RvsSessionState rvsSessionState) {
            //TODO
            EventBus.getDefault().post(new SessionStateEvent(rvsSessionState));
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
        }

        @Override
        public void onStreamerConfigState(long l, StreamerConfigState streamerConfigState) {
            //TODO
            EventBus.getDefault().post(new StreamerConfigChangedEvent(streamerConfigState));
        }
    };

    /**
     * 构造方法
     * @param context
     */
    private Communication(Context context){
        this.context = context;
        this.viewer = Viewer.getViewer();
        loadSdkLib();
    }

    /**
     * 该类的初始化工作
     */
    private void init(){
        //初始化SDK
        viewer.init(context, Constant.APP_VERSION_STR, context.getFilesDir().getAbsolutePath(),
                Constant.EXTERNAL_VIDEO_FOLDER_NAME);
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

    //load sdk lib
    private void loadSdkLib()
    {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("avdecoder");
        System.loadLibrary("sdk30");
        System.loadLibrary("viewer30");
    }


    public boolean isHasLogin() {
        return hasLogin;
    }
}

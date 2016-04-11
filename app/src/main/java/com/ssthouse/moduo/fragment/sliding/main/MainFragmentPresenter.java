package com.ssthouse.moduo.fragment.sliding.main;

import android.app.Activity;
import android.content.Context;

import com.ssthouse.moduo.activity.video.CallingActivity;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.view.NetworkStateChangeEvent;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLogoutEvent;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceLoginEvent;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceOnLineEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Presenter层
 * 接受事件{
 *     NetworkStateChangeEvent: 网络状态变化事件
 *     GetBoundDeviceEvent: 获取绑定设备回调
 *     XPGLoginResultEvent: 机智云登录结果回调
 *     UnbindResultEvent: 设备解绑回调
 *     XpgDeviceOnLineEvent: 设备在线状态变化事件回调
 * }
 * Created by ssthouse on 2016/2/15.
 */
public class MainFragmentPresenter {

    //View Model
    private MainFragmentView mMainFragmentView;
    private MainFragmentModel mMainFragmentModel;

    private Context mContext;

    //是否在离线状态
    private boolean isOffline = true;

    public MainFragmentPresenter(Context context, MainFragmentView mainFragmentView) {
        this.mMainFragmentView = mainFragmentView;
        mMainFragmentModel = new MainFragmentModel(context);
        mContext = context;
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化设备连接
     */
    public void initDevice() {
        //未登录---先登录
        if (!XPGController.isLogin()) {
            tryLogin();
            return;
        }
        XPGController.getInstance(mContext)
                .getmCenter()
                .cGetBoundDevices(
                        SettingManager.getInstance(mContext).getUid(),
                        SettingManager.getInstance(mContext).getToken());
    }

    //尝试登陆
    public void tryLogin() {
        //判断是否为匿名登录
        if (SettingManager.getInstance(mContext).isAnonymousUser()) {
            XPGController.getInstance(mContext).getmCenter().cLoginAnonymousUser();
            Timber.e("匿名登录");
        } else {
            XPGController.getInstance(mContext).getmCenter().cLogin(
                    SettingManager.getInstance(mContext).getUserName(),
                    SettingManager.getInstance(mContext).getPassword()
            );
            Timber.e("实名登陆");
        }
    }

    //跳转到视频控制Activity
    public void jump2Video() {
        switch (Communication.getInstance(mContext).getStreamerPresenceState()) {
            case INIT:
                Toast.show("魔哆摄像头不在线");
                break;
            case OFFLINE:
                Toast.show("魔哆摄像头不在线");
                break;
            case USRNAME_PWD_ERR:
                Toast.show("魔哆用户名密码错误");
                break;
            case ONLINE:
                CallingActivity.start(mContext);
                break;
        }
    }

    //网络状态变化的回调
    public void onEventMainThread(NetworkStateChangeEvent event) {
        switch (event.getNetworkState()) {
            case NONE:
                isOffline = true;
                Toast.show("网络连接已断开");
                break;
            case MOBILE:
                //如果之前是离线状态---需要重新连接(视频会不断自动连接---机智云不会--需要手动重新连接)
                if (isOffline) {
                    initDevice();
                    isOffline = false;
                }
                break;
            case WIFI:
                //如果离线---重新连接设备
                if (isOffline) {
                    initDevice();
                    isOffline = false;
                }
                break;
        }
        //离线处理---两个sdk离线
        if (isOffline) {
            XPGController.setLogin(false);
            Communication.setLogin(false);
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    //获取 绑定设备列表 回调
    public void onEventMainThread(GetBoundDeviceEvent event) {
        Timber.e("获取设备列表回调");
        //隐藏dialog
        mMainFragmentView.dismissDialog();
        if (event.isSuccess()) {
            mMainFragmentModel.setCurrentDevice(event);
            Timber.e("设置当前device");
        } else {
            Toast.show("获取绑定设备失败");
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    //sdk登录成功回调
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            //保存登陆数据
            mMainFragmentModel.saveLoginInfo(event);
            //刷新设备
            initDevice();
            Timber.e("机智云---登录成功");
        } else {
            Toast.show("登陆失败");
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    //设备登陆成功回调
    public void onEventMainThread(XpgDeviceLoginEvent event) {
        mMainFragmentView.updateUI();
    }

    //注销回调
    public void onEventMainThread(XPGLogoutEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) mContext, "MainActivity")) {
            return;
        }
        mMainFragmentView.updateUI();
    }

    //解绑设备回调
    public void onEventMainThread(UnbindResultEvent event) {
        //请求设备列表
        XPGController.getInstance(mContext).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(mContext).getUid(),
                SettingManager.getInstance(mContext).getToken());
    }

    //设备是否在线 状态变化 回调
    public void onEventMainThread(XpgDeviceOnLineEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) mContext, "MainActivity")) {
            return;
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}

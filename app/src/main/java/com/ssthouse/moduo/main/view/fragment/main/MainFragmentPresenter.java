package com.ssthouse.moduo.main.view.fragment.main;

import android.app.Activity;
import android.content.Context;

import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.model.bean.device.DeviceData;
import com.ssthouse.moduo.main.model.bean.event.view.NetworkStateChangeEvent;
import com.ssthouse.moduo.main.model.bean.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.main.model.bean.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.main.model.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.model.bean.event.xpg.XpgDeviceStateEvent;
import com.ssthouse.moduo.main.view.activity.XpgControlActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * Presenter层
 * Created by ssthouse on 2016/2/15.
 */
public class MainFragmentPresenter {

    //View Model
    private MainFragmentView mMainFragmentView;
    private MainFragmentModel mMainFragmentModel;

    private Context mContext;

    //是否在离线状态
    private boolean isOffline;

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
        XPGController.getInstance(mContext)
                .getmCenter()
                .cGetBoundDevices(
                        SettingManager.getInstance(mContext).getUid(),
                        SettingManager.getInstance(mContext).getToken());
    }

    /**
     * 网络状态变化的回调
     *
     * @param event
     */
    public void onEventMainThread(NetworkStateChangeEvent event) {
        switch (event.getNetworkState()) {
            case NONE:
                isOffline = true;
                ToastHelper.show(mContext, "网络连接已断开");
                break;
            case MOBILE:
                //如果之前是离线状态---需要重新连接(视频会不断自动连接---机智云不会--需要手动重新连接)
                if (isOffline) {
                    initDevice();
                }
                break;
            case WIFI:
                //如果离线---重新连接设备
                if (isOffline) {
                    initDevice();
                }
                break;
        }
    }

    /**
     * 获取 绑定设备列表 回调
     *
     * @param event
     */
    public void onEventMainThread(GetBoundDeviceEvent event) {
        Timber.e("获取设备列表回调");
        //隐藏dialog
        mMainFragmentView.dismissDialog();
        if (event.isSuccess()) {
            mMainFragmentModel.setCurrentDevice(event);
        } else {
            ToastHelper.show(mContext, "获取绑定设备失败");
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    /**
     * sdk登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            //保存登陆数据
            mMainFragmentModel.saveLoginInfo(event);
            //刷新设备
            initDevice();
            Timber.e("机智云---登录成功");
        } else {
            ToastHelper.show(mContext, "登陆失败");
        }
        //刷新UI
        mMainFragmentView.updateUI();
    }

    /**
     * 设备状态变化回调:
     * 设备登陆成功---代表要跳转到控制界面
     * 直接跳转XPGControlActivity
     *
     * @param event
     */
    public void onEventMainThread(XpgDeviceStateEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) mContext, "MainActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //跳转控制界面
            if (event.getDid().equals(XPGController.getCurrentDevice().getXpgWifiDevice().getDid())) {
                //跳转控制界面
                XpgControlActivity.start(mContext, new DeviceData());
            } else {
                Timber.e("不是当前设备在登陆...");
            }
        } else {
            Timber.e("登陆失败...");
        }
    }

    /**
     * 解绑设备回调
     *
     * @param event
     */
    public void onEventMainThread(UnbindResultEvent event) {
        //请求设备列表
        XPGController.getInstance(mContext).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(mContext).getUid(),
                SettingManager.getInstance(mContext).getToken());
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}

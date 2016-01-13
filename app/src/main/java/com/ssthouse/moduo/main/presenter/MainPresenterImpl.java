package com.ssthouse.moduo.main.presenter;

import android.app.Activity;
import android.content.Context;

import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.ScanUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.bean.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.main.view.MainView;
import com.ssthouse.moduo.cons.Constant;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.device.DeviceData;
import com.ssthouse.moduo.bean.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.bean.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.bean.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XpgDeviceStateEvent;
import com.ssthouse.moduo.main.view.activity.XpgControlActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 实现UI操作
 * Created by ssthouse on 2016/1/10.
 */
public class MainPresenterImpl implements MainPresenter {

    /**
     * MainActivity的引用
     */
    private MainView mainView;

    private Context context;

    /**
     * 传入MainView的构造方法
     *
     * @param mainView
     */
    public MainPresenterImpl(Context context, MainView mainView) {
        this.context = context;
        this.mainView = mainView;
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化设备列表
     */
    @Override
    public void initDeviceList() {
        mainView.showDialog("正在获取设备");
        XPGController.getInstance(context).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(context).getUid(),
                SettingManager.getInstance(context).getToken()
        );
    }

    /**
     * 刷新设备列表
     */
    @Override
    public void refreshDeviceList() {
        XPGController.getInstance(context).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(context).getUid(),
                SettingManager.getInstance(context).getToken()
        );
    }

    /**
     * 增加设备
     *
     * @param context
     */
    @Override
    public void addDevice(Context context) {
        //启动扫描设备二维码Activity
        ScanUtil.startScan(context);
    }

    /**
     * 登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        Timber.e("登陆成功回调");
        if (event.isSuccess()) {
            //改变全局状态
            Constant.isXpgLogin = true;
            Timber.e("机智云---登录成功");
            ToastHelper.show(context, "登陆成功!");
            //保存机智云登陆数据
            SettingManager.getInstance(context).setLoginInfo(event);
            //获取设备
            initDeviceList();
        } else {
            ToastHelper.show(context, "登陆失败");
        }
    }

    /**
     * 添加设备回调
     *
     * @param event
     */
    public void onEventMainThread(ScanDeviceEvent event) {
        Timber.e("扫描Activity回调");
        if (event.isSuccess()) {
            mainView.showDialog("正在绑定设备,请稍候");
            //开始绑定设备
            XPGController.getInstance(context).getmCenter().cBindDevice(
                    SettingManager.getInstance(context).getUid(),
                    SettingManager.getInstance(context).getToken(),
                    event.getDid(),
                    event.getPassCode(),
                    ""
            );
        } else {
            ToastHelper.show(context, "设备绑定失败");
        }
    }

    /**
     * 绑定设备回调
     *
     * @param event
     */
    public void onEventMainThread(DeviceBindResultEvent event) {
        Timber.e("设备绑定回调");
        mainView.dismissDialog();
        if (event.isSuccess()) {
            mainView.showDialog("正在获取设备列表,请稍候");
            ToastHelper.show(context, "设备绑定成功");
            //请求设备列表
            XPGController.getInstance(context).getmCenter().cGetBoundDevices(
                    SettingManager.getInstance(context).getUid(),
                    SettingManager.getInstance(context).getToken());
        } else {
            ToastHelper.show(context, "设备绑定失败");
        }
    }

    /**
     * 解绑设备回调
     *
     * @param event
     */
    public void onEventMainThread(UnbindResultEvent event) {
        //请求设备列表
        XPGController.getInstance(context).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(context).getUid(),
                SettingManager.getInstance(context).getToken());
    }

    /**
     * 获取 绑定设备列表 回调
     *
     * @param event
     */
    public void onEventMainThread(GetBoundDeviceEvent event) {
        Timber.e("获取设备列表回调");
        //隐藏dialog
        mainView.dismissDialog();
        if (event.isSuccess()) {
            //刷新主界面lv列表
            ToastHelper.show(context, "获取绑定设备成功,设备数目:\t" + event.getXpgDeviceList().size());
        } else {
            ToastHelper.show(context, "获取设备列表失败");
        }
        //隐藏等待dialog
        mainView.dismissDialog();
    }


    /**
     * 设备状态变化回调:
     * 设备登陆成功---代表要跳转到控制界面
     * 直接跳转XPGControlActivity
     *
     * @param event
     */
    public void onEventMainThread(XpgDeviceStateEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) context, "MainActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //跳转控制界面
            for (Device device : XPGController.getDeviceList()) {
                if (device.getXpgWifiDevice().getDid().equals(event.getDid())) {
                    XPGController.setCurrentDevice(device);
                    //直接跳转控制界面
                    XpgControlActivity.start(context, new DeviceData());
                }
            }
        }
    }

    @Override
    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}

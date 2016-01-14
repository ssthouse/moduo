package com.ssthouse.moduo.main.presenter;

import android.content.Context;

import com.ssthouse.moduo.bean.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.bean.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.control.util.ScanUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.MainView;

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

    @Override
    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}

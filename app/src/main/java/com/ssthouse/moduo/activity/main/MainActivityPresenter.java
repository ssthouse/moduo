package com.ssthouse.moduo.activity.main;

import android.app.Activity;
import android.content.Context;

import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLogoutEvent;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Presenter
 * 响应Event{
 *     ScanDeviceEvent: 扫描二维码回调
 *     DeviceBindResultEvent    设备绑定回调
 *     XPGLoginResultEvent  机智云登录回调
 *     XPGLogoutEvent   机智云注销回调
 * }
 * Created by ssthouse on 2016/2/15.
 */
public class MainActivityPresenter {

    private Context mContext;

    private MainActivityView mMainActivityView;

    private static final String DEFAULT_MODUO_REMARK = "我的魔哆";

    //构造方法
    public MainActivityPresenter(Context mContext, MainActivityView mainActivityView) {
        this.mContext = mContext;
        this.mMainActivityView = mainActivityView;
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化设备连接
     */
    private void initDevice() {
        XPGController.getInstance(mContext)
                .getmCenter()
                .cGetBoundDevices(
                        SettingManager.getInstance(mContext).getUid(),
                        SettingManager.getInstance(mContext).getToken());
    }

    /**
     * 扫描设备回调
     *
     * @param event
     */
    public void onEventMainThread(ScanDeviceEvent event) {
        Timber.e("扫描Activity回调");
        if (event.isSuccess()) {
            mMainActivityView.showWaitDialog("正在绑定设备,请稍候");
            //开始绑定设备
            XPGController.getInstance(mContext).getmCenter().cBindDevice(
                    SettingManager.getInstance(mContext).getUid(),
                    SettingManager.getInstance(mContext).getToken(),
                    event.getDid(),
                    event.getPassCode(),
                    DEFAULT_MODUO_REMARK);
        }
    }

    /**
     * 绑定设备回调
     *
     * @param event
     */
    public void onEventMainThread(final DeviceBindResultEvent event) {
        Timber.e("设备绑定回调");
        mMainActivityView.dismissWaitDialog();
        if (event.isSuccess()) {
            Toast.show("设备绑定成功, 正在自动登陆");
            //获取设备Info信息
            CloudUtil.getDeviceFromCloud(event.getDid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ModuoInfo>() {
                        @Override
                        public void call(ModuoInfo moduoInfo) {
                            if (moduoInfo == null) {
                                Toast.show("服务器获取魔哆设备信息为空:did   " + event.getDid());
                                return;
                            }
                            //保存设备信息到本地
                            SettingManager.getInstance(mContext)
                                    .setCurrentModuoInfo(moduoInfo);
                            //保存设备信息到本地后---请求设备列表
                            XPGController.getInstance(mContext).getmCenter().cGetBoundDevices(
                                    SettingManager.getInstance(mContext).getUid(),
                                    SettingManager.getInstance(mContext).getToken());
                        }
                    });
        } else {
            Toast.show("设备绑定失败");
        }
        mMainActivityView.updateUI();
    }

    //登录回调
    public void onEventMainThread(XPGLoginResultEvent event) {
        //保存登陆数据(不管登录成功没)
        mMainActivityView.updateUI();
    }

    //注销回调
    public void onEventMainThread(XPGLogoutEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) mContext, "MainActivity")) {
            return;
        }
        mMainActivityView.dismissWaitDialog();
        mMainActivityView.dismissConfirmLogoutDialog();
        mMainActivityView.updateUI();
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}

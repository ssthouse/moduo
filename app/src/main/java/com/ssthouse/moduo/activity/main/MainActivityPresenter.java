package com.ssthouse.moduo.activity.main;

import android.content.Context;

import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Presenter
 * Created by ssthouse on 2016/2/15.
 */
public class MainActivityPresenter {

    private Context mContext;

    private MainActivityView mMainActivityView;
    private MainActivityModel mMainActivityModel;

    //构造方法
    public MainActivityPresenter(Context mContext, MainActivityView mainActivityView) {
        this.mContext = mContext;
        this.mMainActivityView = mainActivityView;
        mMainActivityModel = new MainActivityModel();
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
            mMainActivityView.showDialog("正在绑定设备,请稍候");
            //将扫描到设备数据保存至cloud
            CloudUtil.saveDeviceToCloud(new ModuoInfo(event.getDid(),
                    event.getPassCode(),
                    event.getCid(),
                    event.getVideoUsername(),
                    event.getVideoPassword()));
            //开始绑定设备
            XPGController.getInstance(mContext).getmCenter().cBindDevice(
                    SettingManager.getInstance(mContext).getUid(),
                    SettingManager.getInstance(mContext).getToken(),
                    event.getDid(),
                    event.getPassCode(),
                    "");
        }
    }

    /**
     * 绑定设备回调
     *
     * @param event
     */
    public void onEventMainThread(final DeviceBindResultEvent event) {
        Timber.e("设备绑定回调");
        mMainActivityView.dismissDialog();
        if (event.isSuccess()) {
            ToastHelper.show(mContext, "设备绑定成功");
            //获取设备Info信息
            mMainActivityModel.getUserInfo(event.getDid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ModuoInfo>() {
                        @Override
                        public void call(ModuoInfo moduoInfo) {
                            if (moduoInfo == null) {
                                Timber.e("服务器获取魔哆设备信息为空:did   " + event.getDid());
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
            ToastHelper.show(mContext, "设备绑定失败");
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }
}

package com.ssthouse.moduo.fragment.sliding.main;

import android.content.Context;

import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.bean.device.Device;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Model
 * 主要功能{
 *     保存当前登录设备
 *     保存当前用户登录缓存数据
 * }
 * Created by ssthouse on 2016/2/15.
 */
public class MainFragmentModel {

    private Context mContext;

    public MainFragmentModel(Context mContext) {
        this.mContext = mContext;
    }

    //根据获取设备列表的event---设置当前device
    public void setCurrentDevice(final GetBoundDeviceEvent event) {
        //如果未绑定设备---需要先绑定
        if (event.getXpgDeviceList().size() <= 0) {
            Toast.show("当前未绑定设备,请先进行绑定");
            Timber.e("我还没有绑定过设备");
            return;
        }
        //将当前绑定设备找出---设为currentDevice
        final SettingManager settingManager = SettingManager.getInstance(mContext);
        if (settingManager.hasLocalModuo()) {
            //找到之前操作的设备
            for (XPGWifiDevice xpgDevice : event.getXpgDeviceList()) {
                if (xpgDevice.getDid().equals(settingManager.getCurrentDid())) {
                    XPGController.setCurrentDevice(Device.getLocalDevice(mContext, xpgDevice));
                    Timber.e("找到之前操作过的设备");
                }
            }
            //如果列表中没有找到本地的魔哆
            if (XPGController.getCurrentDevice() == null) {
                Timber.e("之前没有操作过---我吧第一个设备设为了默认操作设备");
                //云端获取设备数据
                CloudUtil.getDeviceFromCloud(event.getXpgDeviceList().get(0).getDid())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ModuoInfo>() {
                            @Override
                            public void call(ModuoInfo moduoInfo) {
                                if (moduoInfo == null) {
                                    Toast.show("获取服务器设备数据失败");
                                } else {
                                    settingManager.setCurrentModuoInfo(moduoInfo);
                                    XPGController.setCurrentDevice(Device.getLocalDevice(mContext, event.getXpgDeviceList().get(0)));
                                }
                            }
                        });
            }
        } else {
            Timber.e("之前没有操作过---我吧第一个设备设为了默认操作设备");
            //云端获取设备数据
            CloudUtil.getDeviceFromCloud(event.getXpgDeviceList().get(0).getDid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ModuoInfo>() {
                        @Override
                        public void call(ModuoInfo moduoInfo) {
                            if (moduoInfo == null) {
                                Toast.show("获取服务器设备数据失败");
                            } else {
                                settingManager.setCurrentModuoInfo(moduoInfo);
                                XPGController.setCurrentDevice(Device.getLocalDevice(mContext, event.getXpgDeviceList().get(0)));
                            }
                        }
                    });
        }
        //设置当前设备监听器
        XPGController.refreshCurrentDeviceListener(mContext);
        //登陆当前设备
        XPGController.loginCurrentDevice();
        //登陆视频sdk
        Communication.getInstance(mContext)
                .addStreamer(XPGController.getCurrentDevice());
    }

    //保存登陆数据
    public void saveLoginInfo(XPGLoginResultEvent event) {
        //保存机智云登陆token数据
        SettingManager.getInstance(mContext).setLoginCacheInfo(event);
    }
}

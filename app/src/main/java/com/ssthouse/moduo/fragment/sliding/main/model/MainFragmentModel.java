package com.ssthouse.moduo.fragment.sliding.main.model;

import android.content.Context;

import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
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
 * Created by ssthouse on 2016/2/15.
 */
public class MainFragmentModel {

    private Context mContext;

    public MainFragmentModel(Context mContext) {
        this.mContext = mContext;
    }

    //根据获取设备列表的event---设置当前device
    public void setCurrentDevice(GetBoundDeviceEvent event) {
        //如果未绑定设备---需要先绑定
        if (event.getXpgDeviceList().size() <= 0) {
            ToastHelper.show(mContext, "当前未绑定设备,请先进行绑定");
            Timber.e("我还没有绑定过设备");
            return;
        }
        //将当前绑定设备找出---设为currentDevice
        if (SettingManager.getInstance(mContext).hasLocalModuo()) {
            //todo---找到之前操作的设备
            for (XPGWifiDevice xpgDevice : event.getXpgDeviceList()) {
                if (xpgDevice.getDid().equals(SettingManager.getInstance(mContext).getCurrentDid())) {
                    XPGController.setCurrentDevice(Device.getLocalDevice(mContext, xpgDevice));
                    Timber.e("找到之前操作过的设备");
                }
            }
        } else {
            XPGController.setCurrentDevice(mContext, event.getXpgDeviceList().get(0));
            Timber.e("之前没有操作过---我吧第一个设备设为了默认操作设备");
            //todo---因为之前没有操作过---本地应该没有视频参数---需要向云端获取设备参数
            CloudUtil.getDeviceFromCloud(event.getXpgDeviceList().get(0).getDid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ModuoInfo>() {
                        @Override
                        public void call(ModuoInfo moduoInfo) {
                            if (moduoInfo == null) {
                                ToastHelper.show(mContext, "获取服务器设备数据失败");
                            } else {
                                SettingManager.getInstance(mContext).setCurrentModuoInfo(moduoInfo);
                            }
                        }
                    });
        }
        //设置监听器
        XPGController.refreshCurrentDeviceListener(mContext);
        //todo---登陆当前设备
        XPGController.getInstance(mContext).loginCurrentDevice();
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

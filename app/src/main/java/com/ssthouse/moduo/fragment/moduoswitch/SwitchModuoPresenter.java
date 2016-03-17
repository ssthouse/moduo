package com.ssthouse.moduo.fragment.moduoswitch;

import android.app.Activity;
import android.content.Context;

import com.ssthouse.moduo.activity.SwitchModuoActivity;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceLoginEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Presenter层
 * Created by ssthouse on 2016/3/6.
 */
public class SwitchModuoPresenter {

    private SwitchFragmentView mSwitchFragmentView;

    private Context mContext;

    //list---数据列表
    private List<XPGWifiDevice> xpgWifiDeviceList = new ArrayList<>();

    //当前长按的position
    private int currentLongClickPosition;
    //当前点按的position
    private int currentClickPosition;
    //当前设备position
    private int currentModuoPosition = -1;

    public SwitchModuoPresenter(SwitchFragmentView mSwitchFragmentView, Context mContext) {
        this.mSwitchFragmentView = mSwitchFragmentView;
        this.mContext = mContext;
        EventBus.getDefault().register(this);
    }

    //获取设备列表
    public void getDeviceList() {
        //获取设备数据
        SettingManager settingManager = SettingManager.getInstance(mContext);
        XPGController.getInstance(mContext).getmCenter().cGetBoundDevices(
                settingManager.getUid(), settingManager.getToken()
        );
    }

    //获取设备列表回调
    public void onEventMainThread(GetBoundDeviceEvent event) {
        mSwitchFragmentView.dismissWaitDialog();
        if (!ActivityUtil.isTopActivity((Activity) mContext, "SwitchModuoActivity")) {
            return;
        }
        if (!event.isSuccess() || event.getXpgDeviceList() == null) {
            Toast.show("获取设备列表失败");
            mSwitchFragmentView.showLoadErr();
            return;
        }
        if (event.getXpgDeviceList().size() == 0) {
            Toast.show("当前未绑定魔哆设备");
            mSwitchFragmentView.showLoadErr();
            return;
        }
        //更新当前设备列表
        xpgWifiDeviceList = event.getXpgDeviceList();
        mSwitchFragmentView.showDeviceList();
    }

    //绑定设备回调
    public void onEventMainThread(DeviceBindResultEvent event) {
        mSwitchFragmentView.dismissWaitDialog();
        if (!ActivityUtil.isTopActivity((Activity) mContext, "SwitchModuoActivity")) {
            return;
        }
        //修改备注后---重新绑定失败
        if (!event.isSuccess()) {
            Toast.show("备注修改失败, 请稍候重试");
            mSwitchFragmentView.dismissChangeRemarkDialog();
            return;
        }
        Toast.show("备注修改成功");
        //退出Activity
        SwitchModuoActivity activity = (SwitchModuoActivity) mContext;
        activity.finish();
    }

    //设备登陆结果回调
    public void onEventMainThread(XpgDeviceLoginEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) mContext, "SwitchModuoActivity")) {
            return;
        }
        mSwitchFragmentView.dismissWaitDialog();
        //登陆成功---且是当前点击的设备
        if (xpgWifiDeviceList.size() == 0) {
            Timber.e("xogDeviceList is not initialed");
            return;
        }
        //判断是否为点击的list中的魔哆
        if (event.isSuccess() && event.getDid().equals(xpgWifiDeviceList.get(currentClickPosition).getDid())) {
            //切换登设备成功---改变本地魔哆
            CloudUtil.getDeviceFromCloud(event.getDid())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ModuoInfo>() {
                        @Override
                        public void call(ModuoInfo moduoInfo) {
                            if (moduoInfo == null) {
                                Toast.show("服务器端数据获取失败, 请稍候重试");
                                return;
                            }
                            //更新本地魔哆数据---刷新界面
                            SettingManager.getInstance(mContext).setCurrentModuoInfo(moduoInfo);
                            mSwitchFragmentView.showLoading();
                            getDeviceList();
                        }
                    });
        }
    }

    //获取当前设备列表
    public List<XPGWifiDevice> getXpgWifiDeviceList() {
        return xpgWifiDeviceList;
    }

    public int getCurrentLongClickPosition() {
        return currentLongClickPosition;
    }

    public int getCurrentClickPosition() {
        return currentClickPosition;
    }

    public int getCurrentModuoPosition() {
        return currentModuoPosition;
    }

    public void setCurrentLongClickPosition(int currentLongClickPosition) {
        this.currentLongClickPosition = currentLongClickPosition;
    }

    public void setCurrentClickPosition(int currentClickPosition) {
        this.currentClickPosition = currentClickPosition;
    }

    public void setCurrentModuoPosition(int currentModuoPosition) {
        this.currentModuoPosition = currentModuoPosition;
    }

    public void destory() {
        EventBus.getDefault().unregister(this);
    }
}

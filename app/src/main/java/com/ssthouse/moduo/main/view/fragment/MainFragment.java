package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.device.DeviceData;
import com.ssthouse.moduo.bean.event.xpg.XpgDeviceStateEvent;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.XpgControlActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 主界面
 * Created by ssthouse on 2016/1/13.
 */
public class MainFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Timber.e("onCreate");
        //发出设备连接请求
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initView(rootView);
        Timber.e("onCreateView");
        return rootView;
    }



    /**
     * 设备状态变化回调:
     * 设备登陆成功---代表要跳转到控制界面
     * 直接跳转XPGControlActivity
     *
     * @param event
     */
    public void onEventMainThread(XpgDeviceStateEvent event) {
        if (event.isSuccess()) {
            //跳转控制界面
            for (Device device : XPGController.getDeviceList()) {
                if (device.getXpgWifiDevice().getDid().equals(event.getDid())) {
                    XPGController.setCurrentXpgWifiDevice(device.getXpgWifiDevice());
                    //直接跳转控制界面
                    XpgControlActivity.start(getActivity(), new DeviceData());
                }
            }
        }
    }

    private void initView(View rootView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

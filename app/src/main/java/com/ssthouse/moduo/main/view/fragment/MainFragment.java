package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.device.DeviceData;
import com.ssthouse.moduo.bean.event.xpg.XpgDeviceStateEvent;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.VideoActivity;
import com.ssthouse.moduo.main.view.activity.XpgControlActivity;

import de.greenrobot.event.EventBus;

/**
 * 主界面
 * Created by ssthouse on 2016/1/13.
 */
public class MainFragment extends Fragment {

    private ImageView ivHomeControl;
    private ImageView ivVideo;
    private ImageView ivMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initView(rootView);
        //todo---发出连接设备请求
        return rootView;
    }

    private void initView(View rootView) {
        ivHomeControl = (ImageView) rootView.findViewById(R.id.id_iv_home_control);
        ivHomeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivVideo = (ImageView) rootView.findViewById(R.id.id_iv_video);
        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivMessage = (ImageView) rootView.findViewById(R.id.id_iv_message);
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoActivity.start(getContext(), XPGController.getCurrentDevice().getCidNumber());
            }
        });
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
                    XPGController.setCurrentDevice(device);
                    //直接跳转控制界面
                    XpgControlActivity.start(getActivity(), new DeviceData());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

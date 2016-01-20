package com.ssthouse.moduo.main.view.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.device.DeviceData;
import com.ssthouse.moduo.bean.event.view.NetworkStateChangeEvent;
import com.ssthouse.moduo.bean.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.bean.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XpgDeviceStateEvent;
import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.util.CloudUtil;
import com.ssthouse.moduo.main.control.util.NetUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.video.Communication;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.HomeControlActivity;
import com.ssthouse.moduo.main.view.activity.MsgCenterActivity;
import com.ssthouse.moduo.main.view.activity.VideoActivity;
import com.ssthouse.moduo.main.view.activity.XpgControlActivity;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 主界面
 * Created by ssthouse on 2016/1/13.
 */
public class MainFragment extends Fragment implements IFragmentUI {

    private ImageView ivHomeControl;
    private ImageView ivVideo;
    private ImageView ivMessage;

    /**
     * 魔哆状态
     */
    private TextView tvModuoState;
    private MaterialDialog waitDialog;

    /**
     * 是否在离线状态
     */
    private boolean isOffline;

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
        //查看网络状态
        isOffline = NetUtil.isConnected(getActivity());
        initView(rootView);
        initDevice();
        return rootView;
    }

    private void initView(View rootView) {
        ivHomeControl = (ImageView) rootView.findViewById(R.id.id_iv_home_control);
        ivHomeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo---启动家居控制activity
                if (XPGController.getCurrentDevice() == null) {
                    ToastHelper.show(getContext(), "当前没有设备连接");
                } else {
                    HomeControlActivity.start(getContext());
//                    XPGController.getCurrentDevice().getXpgWifiDevice().login(
//                            SettingManager.getInstance(getContext()).getUid(),
//                            SettingManager.getInstance(getContext()).getToken()
//                    );
                }
            }
        });

        ivVideo = (ImageView) rootView.findViewById(R.id.id_iv_video);
        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/1/14 判断当前视频连接状态
                if (XPGController.getCurrentDevice() == null) {
                    ToastHelper.show(getContext(), "当前没有设备连接");
                } else {
                    VideoActivity.start(getContext(),
                            Long.parseLong(XPGController.getCurrentDevice().getVideoCidNumber()));
                }
            }
        });

        ivMessage = (ImageView) rootView.findViewById(R.id.id_iv_message);
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/1/14 消息中心
                MsgCenterActivity.start(getContext());
            }
        });

        tvModuoState = (TextView) rootView.findViewById(R.id.id_tv_moduo_state);

        waitDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .autoDismiss(false)
                .build();
    }


    @Override
    public void updateUI() {
        //是否账号登陆
        if (!SettingManager.getInstance(getContext()).isLogined()) {
            tvModuoState.setText("未登录");
            return;
        }
        if (XPGController.getCurrentDevice() == null) {
            tvModuoState.setText("未连接魔哆");
        } else {
            tvModuoState.setText("魔哆连接成功,欢迎使用");
        }
    }

    private void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    /**
     * 初始化设备连接
     */
    private void initDevice() {
        XPGController.getInstance(getContext())
                .getmCenter()
                .cGetBoundDevices(
                        SettingManager.getInstance(getContext()).getUid(),
                        SettingManager.getInstance(getContext()).getToken());
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
                ToastHelper.show(getContext(), "网络连接已断开");
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
        waitDialog.dismiss();
        if (event.isSuccess()) {
            //如果未绑定设备---需要先绑定
            if (event.getXpgDeviceList().size() <= 0) {
                ToastHelper.show(getContext(), "当前未绑定设备,请先进行绑定");
                Timber.e("我还没有绑定过设备");
                return;
            }
            //将当前绑定设备找出---设为currentDevice
            if (SettingManager.getInstance(getContext()).hasLocalModuo()) {
                //找到之前操作的设备
                for (XPGWifiDevice xpgDevice : event.getXpgDeviceList()) {
                    if (xpgDevice.getDid().equals(SettingManager.getInstance(getContext()).getCurrentDid())) {
                        XPGController.setCurrentDevice(Device.getLocalDevice(getContext(), xpgDevice));
                        Timber.e("找到之前操作过的设备");
                    }
                }
            } else {
                XPGController.setCurrentDevice(getContext(), event.getXpgDeviceList().get(0));
                Timber.e("之前没有操作过---我吧第一个设备设为了默认操作设备");
            }
            //设置监听器
            XPGController.refreshCurrentDeviceListener(getContext());
            //登陆视频sdk
            Communication.getInstance(getContext())
                    .addStreamer(XPGController.getCurrentDevice());
        } else {
            ToastHelper.show(getContext(), "获取绑定设备失败");
        }
        //刷新UI
        updateUI();
    }

    /**
     * sdk登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            tvModuoState.setText("用户登陆成功");
            CloudUtil.updateUserInfo(getContext(), SettingManager.getInstance(getContext()).getUserName());
            //保存机智云登陆数据
            SettingManager.getInstance(getContext()).setLoginCacheInfo(event);
            //获取设备
            initDevice();
            //toast提示
            Timber.e("机智云---登录成功");
        } else {
            ToastHelper.show(getContext(), "登陆失败");
        }
        //刷新UI
        updateUI();
    }

    /**
     * 设备状态变化回调:
     * 设备登陆成功---代表要跳转到控制界面
     * 直接跳转XPGControlActivity
     *
     * @param event
     */
    public void onEventMainThread(XpgDeviceStateEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //跳转控制界面
            if (event.getDid().equals(XPGController.getCurrentDevice().getXpgWifiDevice().getDid())) {
                //跳转控制界面
                XpgControlActivity.start(getActivity(), new DeviceData());
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
        XPGController.getInstance(getContext()).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(getContext()).getUid(),
                SettingManager.getInstance(getContext()).getToken());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

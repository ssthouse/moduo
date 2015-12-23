package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.SettingManager;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.setting.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.setting.UnbindResultEvent;
import com.ssthouse.moduo.model.event.setting.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * loading界面:
 * 程序入口
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * 用于判断---是否已经把无效的设备删除干净
     */
    private int wastedDeviceNum = 0;

    /**
     * 启动当前activity
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoadingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        //匿名登录
        XPGController.getInstance(this).getmCenter().cLoginAnonymousUser();

        //加载视频对话sdk
        loadSdkLib();
        Communication.getInstance(this);
    }

    //加载视频对话sdk
    private void loadSdkLib() {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("avdecoder");
        System.loadLibrary("sdk30");
        System.loadLibrary("viewer30");
    }

    /**
     * 视频直播---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(ViewerLoginResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("视频直播---登录成功");
        } else {
            ToastHelper.show(this, "登陆视频sdk失败");
        }
    }

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(this, "LoadingActivity")) {
            return;
        }
        if (event.isSuccess()) {
            Timber.e("机智云---登录成功");
            //保存机智云登陆数据
            SettingManager.getInstance(this).setLoginInfo(event);
            //如果是第一次---还需要删除废除设备---否则跳转MainActivity
            if (PreferenceHelper.getInstance(this).isFistIn()) {
                XPGController.getInstance(this).getmCenter()
                        .cGetBoundDevices(
                        SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken());
                //不是第一次了
                PreferenceHelper.getInstance(this).setIsFistIn(false);
            } else {
                jumpToMainActivity();
            }
        } else {
            jumpToMainActivity();
            Timber.e("机智云---登录失败");
            ToastHelper.show(this, "机智云---登录失败");
        }
    }

    /**
     * 获取绑定设备的回调
     *
     * @param event
     */
    public void onEventMainThread(GetBoundDeviceEvent event) {
        if (!ActivityUtil.isTopActivity(this, "LoadingActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //初始化---已废除设备数据
            wastedDeviceNum = event.getXpgDeviceList().size();
            //如果为0---直接跳转
            if (wastedDeviceNum == 0) {
                jumpToMainActivity();
                return;
            }
            Timber.e("已经绑定的设别数目为\t" + wastedDeviceNum);
            for (XPGWifiDevice xpgWifiDevice : event.getXpgDeviceList()) {
                //删除设备
                XPGController.getInstance(this).getmCenter().cUnbindDevice(
                        SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken(),
                        xpgWifiDevice.getDid(),
                        xpgWifiDevice.getPasscode());
                Timber.e("尝试解绑");
            }
        }
    }

    /**
     * 解绑设备的回调
     *
     * @param event
     */
    public void onEventMainThread(UnbindResultEvent event) {
        if (!ActivityUtil.isTopActivity(this, "LoadingActivity")) {
            return;
        }
        //// TODO: 2015/12/23 不管成不成功--都要减少数目
        wastedDeviceNum--;
        Timber.e("又少一个");
        if (wastedDeviceNum <= 0) {
            //跳转MainActivity
            jumpToMainActivity();
        } else {
            //解绑一个少一个
            wastedDeviceNum--;
        }
    }

    private void jumpToMainActivity() {
        MainActivity.start(this, true);
        EventBus.getDefault().unregister(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

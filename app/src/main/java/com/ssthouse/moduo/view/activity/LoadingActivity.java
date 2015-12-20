package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.SettingManager;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.StringUtils;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.setting.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;
import com.ssthouse.moduo.view.activity.account.RegisterActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * loading界面:
 * 程序入口
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * 当前登陆成功的平台数目
     */
    private int loginPlatformNum = 0;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        //判断第一次进入---是否注册登陆成功
        if (PreferenceHelper.getInstance(this).isFistIn()
                || StringUtils.isEmpty(SettingManager.getInstance(this).getToken())) {
            RegisterActivity.start(this);
            finish();
            return;
        } else {
            //TODO---登陆机智云平台
            XPGController.getInstance(this).getmCenter()
                    .cLogin(SettingManager.getInstance(this).getUserName(),
                            SettingManager.getInstance(this).getPassword());
        }

        //加载sdk
        loadSdkLib();
        Communication.getInstance(this);
    }

    //load sdk lib
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
            //判断是否两个平台都回调结束
            if (loginPlatformNum > 0) {
                Timber.e("视频直播---登录成功\n转向MainActivity");
                MainActivity.start(this, true);
                finish();
            } else {
                Timber.e("视频直播---登录成功");
                loginPlatformNum += 1;
            }
        } else {
            MainActivity.start(this, false);
            ToastHelper.show(this, "登陆视频sdk失败");
        }
    }

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            //判断是否两个平台都回调结束
            if (loginPlatformNum > 0) {
                Timber.e("机智云---登录成功");
                MainActivity.start(this, true);
                finish();
            } else {
                Timber.e("机智云---登录成功");
                loginPlatformNum += 1;
            }
            //保存数据
            SettingManager.getInstance(this).setLoginInfo(event);
        } else {
            MainActivity.start(this, false);
            Timber.e("机智云---登录失败");
            ToastHelper.show(this, "机智云---登录失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

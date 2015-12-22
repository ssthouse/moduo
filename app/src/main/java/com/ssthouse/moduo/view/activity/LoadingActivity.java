package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.SettingManager;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.setting.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * loading界面:
 * 程序入口
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * 当前登陆成功的平台数目:
     * 两个平台都登陆成功才能跳转
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
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        //todo---注册登陆功能暂时不需要
        //判断第一次进入---是否注册登陆成功
//        if (PreferenceHelper.getInstance(this).isFistIn()
//                || StringUtils.isEmpty(SettingManager.getInstance(this).getToken())) {
//            RegisterActivity.start(this);
//            finish();
//            return;
//        }
        //todo---匿名登录---登陆机智云平台---还是正常的回调
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
            //保存机智云登陆数据
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

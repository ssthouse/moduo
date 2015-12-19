package com.ssthouse.moduo.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.SettingManager;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.StringUtils;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;
import com.ssthouse.moduo.view.activity.account.RegisterActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * loading界面
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        //TODO---判断是否为第一次进去---或者没有登陆
        if (PreferenceHelper.getInstance(this).isFistIn()
                || StringUtils.isEmpty(new SettingManager(this).getToken())) {
            RegisterActivity.start(this);
            finish();
            return;
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
     * 登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(ViewerLoginResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("转向MainActivity");
            MainActivity.start(this, true);
            finish();
        } else {
            MainActivity.start(this, false);
            ToastHelper.show(this, "登陆视频sdk失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

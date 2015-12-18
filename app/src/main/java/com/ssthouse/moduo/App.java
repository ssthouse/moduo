package com.ssthouse.moduo;

import android.app.Application;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import timber.log.Timber;

/**
 * 启动application
 * Created by ssthouse on 2015/12/17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //init log
        Logger.init("ssthouse")
                .setLogLevel(LogLevel.FULL)
                .hideThreadInfo();
        Timber.plant(new Timber.DebugTree());
        //友盟统计
        MobclickAgent.setDebugMode(true);
        //友盟更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        //百度推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                "5cbIdRlHlm10M1IvSAfesaDM");
    }
}

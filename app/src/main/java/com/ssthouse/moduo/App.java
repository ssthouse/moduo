package com.ssthouse.moduo;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

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
        //友盟统计
        MobclickAgent.setDebugMode(true);
        //友盟更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
    }
}

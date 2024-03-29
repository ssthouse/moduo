package com.mingko.moduo;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.avos.avoscloud.AVOSCloud;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.mingko.moduo.control.util.AssertsUtils;
import com.mingko.moduo.control.util.FileUtil;
import com.mingko.moduo.control.util.Toast;
import com.mingko.moduo.model.cons.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xtremeprog.xpgconnect.XPGWifiSDK;

import cn.jpush.android.api.JPushInterface;
import im.fir.sdk.FIR;
import timber.log.Timber;

/**
 * 启动application
 * Created by ssthouse on 2015/12/17
 *
 * APPID = 570e0846 (old 56a6efef)
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56a6efef");

        //极光推送
        JPushInterface.setDebugMode(Constant.isDebug);
        JPushInterface.init(this);

        //初始化 log
        Timber.plant(new Timber.DebugTree());

        //Toast全局初始化
        Toast.init(this);

        //activeAndroid数据库
        ActiveAndroid.initialize(this);

        //友盟统计---更新
        MobclickAgent.setDebugMode(false);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        //初始化机智云sdk
        XPGWifiSDK.sharedInstance().startWithAppID(this, Constant.SettingSdkCons.APP_ID);
        XPGWifiSDK.sharedInstance().setLogLevel(Constant.SettingSdkCons.LOG_LEVEL, Constant.isDebug);

        //复制assert文件夹中的json文件到设备安装目录。json文件是解析数据点必备的文件
        AssertsUtils.copyAllAssertToCacheFolder(this.getApplicationContext());

        //LeanCloud初始化
        AVOSCloud.initialize(this, Constant.LeanCloudCons.APPLICATION_ID, Constant.LeanCloudCons.CLIENT_KEY);

        //初始化sd卡文件路径
        FileUtil.initModuoFolder();

        //BugHd
        FIR.init(this);
    }
}

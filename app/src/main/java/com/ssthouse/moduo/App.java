package com.ssthouse.moduo;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.avos.avoscloud.AVOSCloud;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.ssthouse.moduo.control.util.AssertsUtils;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.cons.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xtremeprog.xpgconnect.XPGWifiSDK;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import im.fir.sdk.FIR;
import timber.log.Timber;

/**
 * 启动application
 * Created by ssthouse on 2015/12/17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //讯飞语音
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56a6efef");
        //极光推送
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        //初始化 log
        Timber.plant(new Timber.DebugTree());
        //Toast全局初始化
        Toast.init(this);
        //activeAndroid数据库
        ActiveAndroid.initialize(this);
        //友盟统计---更新
        MobclickAgent.setDebugMode(true);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        //初始化机智云sdk
        XPGWifiSDK.sharedInstance().startWithAppID(this, Constant.SettingSdkCons.APP_ID);
        XPGWifiSDK.sharedInstance().setLogLevel(Constant.SettingSdkCons.LOG_LEVEL,
                Constant.SettingSdkCons.LOG_FILE_NAME, Constant.isDebug);
        XPGController.getInstance(this);
        try {
            //复制assert文件夹中的json文件到设备安装目录。json文件是解析数据点必备的文件
            AssertsUtils.copyAllAssertToCacheFolder(this.getApplicationContext());
        } catch (IOException e) {
            Timber.e("复制出错");
            e.printStackTrace();
        }
        //leancloud
        AVOSCloud.initialize(this, "w0nIsCHtpfX5cxQbfiqvnVuz-gzGzoHsz", "SbGChPAMSHouaRtkV8OO8oVk");
        //初始化sd卡文件路径
        FileUtil.initModuoFolder();
        //BugHd
        FIR.init(this);
    }
}

package com.ssthouse.moduo;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.avos.avoscloud.AVOSCloud;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.ssthouse.moduo.main.model.cons.Constant;
import com.ssthouse.moduo.main.control.util.AssertsUtils;
import com.ssthouse.moduo.main.control.util.FileUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xtremeprog.xpgconnect.XPGWifiSDK;

import java.io.IOException;

import timber.log.Timber;

/**
 * 启动application
 * Created by ssthouse on 2015/12/17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化 log
        Timber.plant(new Timber.DebugTree());
        //activeAndroid数据库
        ActiveAndroid.initialize(this);
        //友盟统计
        MobclickAgent.setDebugMode(true);
        //友盟更新
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);
        //百度推送
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                "5cbIdRlHlm10M1IvSAfesaDM");
        // 初始化sdk,传入appId,登录机智云官方网站查看产品信息获得 AppID
        XPGWifiSDK.sharedInstance().startWithAppID(this, Constant.SettingSdkCons.APP_ID);
        // 设定日志打印级别,日志保存文件名，是否在后台打印数据.
        XPGWifiSDK.sharedInstance().setLogLevel(Constant.SettingSdkCons.LOG_LEVEL,
                Constant.SettingSdkCons.LOG_FILE_NAME, Constant.isDebug);
        //复制xpg配置文件
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
    }
}

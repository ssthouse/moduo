package com.mingko.moduo.model.cons;

import com.xtremeprog.xpgconnect.XPGWifiSDK;

/**
 * app配置常量
 * Created by ssthouse on 2015/12/17.
 */
public class Constant {

    /*
    全局标志位
     */
    /**
     * 是否在开发环境
     */
    public static final boolean isDebug = true;

    /**
     * 应用版本名称
     */
    public static final String APP_VERSION_STR = "moduo_1.0.0";

    /**
     * 外部存储的视频存放路径
     */
    public static final String EXTERNAL_VIDEO_FOLDER_NAME = "moduo_video";

    /**
     * 视频对话SDK常量
     */
    public interface VideoSdkCons {
        String companyID = "8fc26db05242474fae3e0368868a58d7";
        long companyKey = 1460513273954L;
        String appID = "940ee67195014a4083498f44d376f1de14";
        //采集端需要的license
        String license = "";
    }

    /**
     * 机智云配置常量
     */
    public interface SettingSdkCons {
        String PRODUCT_KEY = "6bd8b6a5281048df90fbaf1a293b30cb";
        String APP_ID = "6e31155109824950a36b5b7bd703c071";
        //日志等级
        //// FIXME: 2016/4/26 此处用于调试，故参数为 XPGWifiSDK.XPGWifiLogLevel.XPGWifiLogLevelError
        XPGWifiSDK.XPGWifiLogLevel LOG_LEVEL = XPGWifiSDK.XPGWifiLogLevel.XPGWifiLogLevelError;
        //日志文件名
        String LOG_FILE_NAME = "xpg_log_file.log";
    }

    /**
     * LeanCloud常量配置
     */
    public interface LeanCloudCons{
        String APPLICATION_ID = "w0nIsCHtpfX5cxQbfiqvnVuz-gzGzoHsz";
        String CLIENT_KEY = "SbGChPAMSHouaRtkV8OO8oVk";
    }
}

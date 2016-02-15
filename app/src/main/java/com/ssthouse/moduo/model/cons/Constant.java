package com.ssthouse.moduo.model.cons;

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
        //公司id
        String companyID = "95e629f0d5a84a83b44cbe57dabc81d6";
        //公司key
        long companyKey = 1450250232247L;
        //应用id
        String appID = "47e0591f4963459682f1e30abc19ded2";
        //采集端需要填写的license
        String license = "";
    }

    /**
     * 机智云配置常量
     */
    public interface SettingSdkCons {
        //应用id
        String APP_ID = "c78fd6a079d14c89b8e0bbd263fc7ef3";
        //product key
        String PRODUCT_KEY = "4be31938bf6948e69c80d0dae2c8af39";
        //日志等级
        XPGWifiSDK.XPGWifiLogLevel LOG_LEVEL = XPGWifiSDK.XPGWifiLogLevel.XPGWifiLogLevelError;
        //日志文件名
        String LOG_FILE_NAME = "xpg_log_file.log";
    }
}

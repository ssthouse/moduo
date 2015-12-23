package com.ssthouse.moduo.control.util;

import android.app.Activity;
import android.content.Context;

import com.google.zxing.integration.android.IntentIntegrator;
import com.ssthouse.moduo.view.activity.ScanActivity;

/**
 * 调用二维码扫描功能
 * Created by ssthouse on 2015/12/19.
 */
public class ScanUtil {

    /**
     * 开启扫描activity
     */
    public static void startScan(Context context) {
        IntentIntegrator integrator = new IntentIntegrator((Activity) context);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    /**
     * 在URL中获取参数值
     * 用于解析URL中想要的数据
     *
     * @param url
     * @param param
     * @return
     */
    public static String getParamFromUrl(String url, String param) {
        String product_key;
        int startIndex = url.indexOf(param + "=");
        //如果二维码没有指定的参数---返回null
        if(startIndex == -1){
            return null;
        }
        startIndex += (param.length() + 1);
        String subString = url.substring(startIndex);
        int endIndex = subString.indexOf("&");
        if (endIndex == -1) {
            product_key = subString;
        } else {
            product_key = subString.substring(0, endIndex);
        }
        return product_key;
    }
}

package com.ssthouse.moduo.control.util;

import android.content.Context;

/**
 * Toast工具类
 * Created by ssthouse on 2016/3/13.
 */
public class Toast {

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static void show(String msg) {
        android.widget.Toast.makeText(mContext, msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        android.widget.Toast.makeText(mContext, msg, android.widget.Toast.LENGTH_LONG).show();
    }

    public static void showModuoNotConnected() {
        show("当前未连接魔哆");
    }

    public static void showOnCoding() {
        show("功能正在开发中");
    }

    public static void showNoInternet() {
        show("连接网络失败, 请重试");
    }
}

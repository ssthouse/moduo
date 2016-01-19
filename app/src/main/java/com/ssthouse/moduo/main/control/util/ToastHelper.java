package com.ssthouse.moduo.main.control.util;

import android.content.Context;
import android.widget.Toast;

/**
 * toast提示工具类
 * Created by ssthouse on 2015/12/15.
 */
public class ToastHelper {

    /**
     * 一条短toast
     *
     * @param context
     * @param msg
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长toast
     *
     * @param context
     * @param msg
     */
    public static void showLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showOnCoding(Context context) {
        Toast.makeText(context, "功能正在开发中", Toast.LENGTH_LONG).show();
    }
}

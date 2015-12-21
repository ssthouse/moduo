package com.ssthouse.moduo.control.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import timber.log.Timber;

/**
 * activity工具类
 * Created by ssthouse on 2015/12/21.
 */
public class ActivityUtil {

    /**
     * 是否是显示在最前面的activity
     *
     * @param activityName 名字
     * @return
     */
    public static  boolean isTopActivity(Activity activity, String activityName) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(activityName)) {
            isTop = true;
        }
        Timber.e("isTop = " + isTop);
        return isTop;
    }
}

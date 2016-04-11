package com.ssthouse.moduo.control.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

/**
 * activity工具类{
 *     检测当前Activity是否在最前, 常用于判断当前Activity是否要接受event事件
 * }
 * Created by ssthouse on 2015/12/21.
 */
public class ActivityUtil {

    /**
     * 是否是显示在最前面的activity
     *
     * @param activityName 名字
     * @return
     */
    public static boolean isTopActivity(Activity activity, String activityName) {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(activityName)) {
            isTop = true;
        }
        return isTop;
    }
}

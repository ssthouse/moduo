package com.ssthouse.moduo.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ssthouse.moduo.activity.ModuoActivity;

import cn.jpush.android.api.JPushInterface;
import timber.log.Timber;

/**
 * 极光推送接收器
 * Created by ssthouse on 2016/2/8.
 */
public class JPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //推送数据
        Bundle bundle = intent.getExtras();
        //推送action
        String action = intent.getAction();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
            Timber.e("推送用户注册action");
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
            Timber.e("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
            Timber.e("收到了自定义通知");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
            Timber.e("用户点击打开了通知");
            // 打开app主界面
            ModuoActivity.start(context);
        } else {
            Timber.e("Unhandled intent - " + intent.getAction());
        }
    }
}

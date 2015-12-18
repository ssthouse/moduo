package com.ssthouse.moduo.control.push;

import android.content.Context;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

import timber.log.Timber;

/**
 * 百度推送回调类
 * Created by ssthouse on 2015/12/18.
 */
public class BaiduMessageReceiver extends PushMessageReceiver {


    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Timber.e(responseString);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {

    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {

        Timber.e("我收到消息了!!!");
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Timber.e("我点击了消息!!!");
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {

    }
}

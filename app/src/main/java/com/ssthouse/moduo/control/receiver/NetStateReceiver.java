package com.ssthouse.moduo.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.model.event.view.NetworkStateChangeEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 网络状态的监听:
 * 抛出事件:    NetworkStateChangeEvent
 * Created by ssthouse on 2015/12/23.
 */
public class NetStateReceiver extends BroadcastReceiver {

    /**
     * 监听的action
     */
    private static String NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        int networkState = NetUtil.NETWORK_NONE;
        if (intent.getAction().equals(NET_CHANGE_ACTION)) {
            networkState = NetUtil.getNetworkState(context);
        }
        switch (networkState) {
            case NetUtil.NETWORK_NONE:
                EventBus.getDefault().post(new NetworkStateChangeEvent(NetworkStateChangeEvent.NetworkState.NONE));
                Timber.e("网络没了");
                break;
            case NetUtil.NETWORK_MOBILE:
                EventBus.getDefault().post(new NetworkStateChangeEvent(NetworkStateChangeEvent.NetworkState.MOBILE));
                Timber.e("在用手机流量");
                break;
            case NetUtil.NETWORK_WIFI:
                EventBus.getDefault().post(new NetworkStateChangeEvent(NetworkStateChangeEvent.NetworkState.WIFI));
                Timber.e("在用WIFI");
                break;
        }
    }
}

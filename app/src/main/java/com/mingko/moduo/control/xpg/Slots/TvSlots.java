package com.mingko.moduo.control.xpg.Slots;

import com.google.gson.JsonObject;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class TvSlots extends SlotsEntity{

    private Object volume;
    private Object channel;
    private String channelName;
    private Object page;
    private String button;

    static {
        byte bParam = byteParam;
        mapParam.put("volume", bParam++);//#2
        mapParam.put("channel", bParam++);
        mapParam.put("channelName", bParam++);
        mapParam.put("page", bParam++);
        mapParam.put("button", bParam++);
        mapParam.put("src", bParam++);
        byte bValue = byteValue;
        mapValue.put("MAX", bValue++);//#15
        mapValue.put("MIN", bValue++);
        mapValue.put("确定", bValue++);
        mapValue.put("返回", bValue++);
        mapValue.put("静音", bValue++);
        mapValue.put("中央一台", bValue++);
    }

    @Override
    public void initParamValue() {
        setParamAndValue("volume", "direct", volume);
        setParamAndValue("channel", "direct", channel);
        setParamAndValue("page", "direct", page);
        setParamAndValue("channelName", channelName);
        setParamAndValue("button", button);
    }

}

package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * preference管理器
 * Created by ssthouse on 2015/12/7.
 */
public class PreferenceHelper {

    /**
     * 单例
     */
    private static PreferenceHelper preferenceHelper;

    /**
     * preference文件名
     */
    private static final String PREFERENCE_NAME = "preference";

    /**
     * 获取单例
     *
     * @param context
     * @return
     */
    public static PreferenceHelper getInstance(Context context) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferenceHelper(context);
        }
        return preferenceHelper;
    }

    /**
     * 当前preference
     */
    private SharedPreferences sharedPreferences;

    /**
     * 构造方法
     *
     * @param context
     */
    private PreferenceHelper(Context context) {
        //初始化sharedPreference
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static final String KEY_IS_FIST_IN = "isFistIn";

    /**
     * 是否第一次进去应用
     *
     * @return
     */
    public boolean isFistIn() {
        return sharedPreferences.getBoolean(KEY_IS_FIST_IN, true);
    }

    /**
     * 设置是否为第一次进入
     *
     * @param isFistIn
     */
    public void setIsFistIn(boolean isFistIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FIST_IN, isFistIn);
        editor.commit();
    }

    /**
     * User数据的key
     */
    private interface DeviceCons {
        String deviceSize = "device_size";
        String devicePrefix = "device_";
    }

    /**
     * 获取本地设备CID列表
     * 在preference中获取list数据
     *
     * @return
     */
    public List<String> getDeviceCidList() {
        List<String> deviceCidList = new ArrayList<>();
        int userSize = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        for (int i = 0; i < userSize; i++) {
            String deviceCidStr = sharedPreferences.getString(DeviceCons.devicePrefix + i, "");
            deviceCidList.add(deviceCidStr);
        }
        return deviceCidList;
    }

    /**
     * 添加设备cid到list中
     */
    public void addDevice(String deviceCidStr) {
        if (deviceCidStr == null) {
            return;
        }
        int userSize = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        int currentDeviceNumber = userSize;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DeviceCons.devicePrefix + currentDeviceNumber, deviceCidStr);
        editor.putInt(DeviceCons.deviceSize, userSize + 1);
        editor.commit();
    }

    /**
     * 删除本地所有的cid的list
     */
    private void deleteAllLocalDeviceCidList() {
        int deviceSize = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < deviceSize; i++) {
            editor.remove(DeviceCons.devicePrefix + i);
        }
        editor.putInt(DeviceCons.deviceSize, 0);
        editor.commit();
    }

    /**
     * 删除某一个userName
     *
     * @param deviceCidStr
     */
    public void deleteDeviceCid(String deviceCidStr) {
        //先获取所有userName的list
        List<String> deviceCidList = getDeviceCidList();
        //将list中尝试删除userName
        boolean success = deviceCidList.remove(deviceCidStr);
        //如果list的size没有变化---就不用再重复添加
        if (success) {
            deleteAllLocalDeviceCidList();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //将剩下的数据添加进去
            for (int i = 0; i < deviceCidList.size(); i++) {
                editor.putString(DeviceCons.devicePrefix + i, deviceCidList.get(i));
            }
            editor.putInt(DeviceCons.deviceSize, deviceCidList.size() - 1);
            editor.commit();
        }
    }
}

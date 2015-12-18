package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssthouse.moduo.model.Device;

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
        //设备名前缀
        String cidPrefix = "cid_";
        //用户名前缀
        String usernamePrefix = "username_";
        //密码前缀
        String passwordPrefix = "password_";
    }

    /**
     * 获取本地的设备
     *
     * @return
     */
    public List<Device> getDeviceList() {
        List<Device> deviceList = new ArrayList<>();
        //获取设备数目
        int deviceSize = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        for (int i = 0; i < deviceSize; i++) {
            long cidNumber = sharedPreferences.getLong(DeviceCons.cidPrefix + i, 0);
            String username = sharedPreferences.getString(DeviceCons.usernamePrefix + i, "");
            String password = sharedPreferences.getString(DeviceCons.passwordPrefix + i, "");
            Device device = new Device(cidNumber, username, password);
            deviceList.add(device);
        }
        return deviceList;
    }

    /**
     * 添加设备
     *
     * @param device
     */
    public void addDevice(Device device) {
        if (device == null) {
            return;
        }
        int currentNumber = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DeviceCons.cidPrefix + currentNumber, device.getCidNumber())
                .putString(DeviceCons.usernamePrefix + currentNumber, device.getUsername())
                .putString(DeviceCons.passwordPrefix + currentNumber, device.getPassword())
                .putInt(DeviceCons.deviceSize, currentNumber + 1)
                .commit();
    }

    /**
     * 删除某一个设备
     */
    public void deleteDevice(Device device) {
        List<Device> deviceList = getDeviceList();
        for (int i = 0; i < deviceList.size(); i++) {
            if (device.getCidNumber() == deviceList.get(i).getCidNumber()
                    && device.getUsername().equals(deviceList.get(i).getUsername())
                    && device.getPassword().equals(deviceList.get(i).getPassword())) {
                deviceList.remove(i);
            }
        }
        //删除所有的数据
        deleteAllDevice();
        //重新添加
        for (Device currentDevice : deviceList) {
            addDevice(currentDevice);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DeviceCons.deviceSize, deviceList.size());
        editor.commit();
    }

    /**
     * 删除所有本地的设备数据
     */
    public void deleteAllDevice() {
        int deviceSize = sharedPreferences.getInt(DeviceCons.deviceSize, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < deviceSize; i++) {
            editor.remove(DeviceCons.cidPrefix + i);
            editor.remove(DeviceCons.usernamePrefix + i);
            editor.remove(DeviceCons.passwordPrefix + i);
        }
        editor.putInt(DeviceCons.deviceSize, 0);
        editor.commit();
    }
}

package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssthouse.moduo.model.Device;

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
     * 需要否根据一个设备did就获取到--它的cid---username---poassword--
     * User数据的key
     */
    private interface DeviceInfoCons {
        //设备名前缀
        String CID_PREFIX = "cid_";
        //用户名前缀
        String USERNAME_PREFIX = "username_";
        //密码前缀
        String PASSWORD_PREFIX = "password_";
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
        //以机智云的did作为key---保存视频sdk的三个参数
        String did = device.getXpgWifiDevice().getDid();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DeviceInfoCons.CID_PREFIX + did, device.getCidNumber())
                .putString(DeviceInfoCons.USERNAME_PREFIX + did, device.getUsername())
                .putString(DeviceInfoCons.PASSWORD_PREFIX + did, device.getPassword())
                .commit();
    }

    /**
     * 获取cid
     *
     * @param did
     * @return
     */
    public long getCidNumber(String did) {
        return sharedPreferences.getLong(DeviceInfoCons.CID_PREFIX + did, 0);
    }

    /**
     * 获取用户名
     *
     * @param did
     * @return
     */
    public String getUsername(String did) {
        return sharedPreferences.getString(DeviceInfoCons.USERNAME_PREFIX + did, null);
    }

    /**
     * 根据did获取密码
     *
     * @param did
     * @return
     */
    public String getPassword(String did) {
        return sharedPreferences.getString(DeviceInfoCons.PASSWORD_PREFIX + did, null);
    }

    /**
     * 将设备参数保存在本地
     *
     * @param did
     * @param cidNumber
     * @param username
     * @param password
     */
    public void addDevice(String did, long cidNumber, String username, String password) {
        //以机智云的did作为key---保存视频sdk的三个参数
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DeviceInfoCons.CID_PREFIX + did, cidNumber)
                .putString(DeviceInfoCons.USERNAME_PREFIX + did, username)
                .putString(DeviceInfoCons.PASSWORD_PREFIX + did, password)
                .commit();
    }


    /**
     * 删除某一个设备
     */
    public void deleteDevice(Device device) {
        if (device == null) {
            return;
        }
        String did = device.getXpgWifiDevice().getDid();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //根据did为key删除数据
        editor.remove(DeviceInfoCons.CID_PREFIX + did)
                .remove(DeviceInfoCons.USERNAME_PREFIX + did)
                .remove(DeviceInfoCons.PASSWORD_PREFIX + did)
                .commit();
    }
}

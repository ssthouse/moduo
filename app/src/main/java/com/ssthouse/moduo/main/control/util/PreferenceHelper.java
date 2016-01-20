package com.ssthouse.moduo.main.control.util;

import android.content.Context;
import android.content.SharedPreferences;

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
     * 需要否根据一个设备did就获取到--它的cid---username---password--
     * User数据的key
     */
    //设备名前缀
    private static String CID_PREFIX = "cid_";
    //用户名前缀
    private static String USERNAME_PREFIX = "username_";
    //密码前缀
    private static String PASSWORD_PREFIX = "password_";

    /**
     * 获取cid
     *
     * @param did
     * @return
     */
    public String getCidNumber(String did) {
        return sharedPreferences.getString(CID_PREFIX + did, null);
    }

    /**
     * 获取用户名
     *
     * @param did
     * @return
     */
    public String getUsername(String did) {
        return sharedPreferences.getString(USERNAME_PREFIX + did, null);
    }

    /**
     * 根据did获取密码
     *
     * @param did
     * @return
     */
    public String getPassword(String did) {
        return sharedPreferences.getString(PASSWORD_PREFIX + did, null);
    }
}

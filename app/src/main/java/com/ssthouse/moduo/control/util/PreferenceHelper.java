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
     * @return
     */
    public boolean isFistIn(){
        return sharedPreferences.getBoolean(KEY_IS_FIST_IN, true);
    }

    /**
     * 设置是否为第一次进入
     * @param isFistIn
     */
    public void setIsFistIn(boolean isFistIn){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_FIST_IN, isFistIn);
        editor.commit();
    }

    /**
     * User数据的key
     */
    private interface UserCons {
        String userSize = "user_size";
        String userPrefix = "user_";
    }

    /**
     * 获取本地的用户用户名List
     * 在preference中获取list数据
     *
     * @return
     */
    public List<String> getLocalUserNameList() {
        List<String> userNameList = new ArrayList<>();
        int userSize = sharedPreferences.getInt(UserCons.userSize, 0);
        for (int i = 0; i < userSize; i++) {
            String userName = sharedPreferences.getString(UserCons.userPrefix + i, "");
            userNameList.add(userName);
        }
        return userNameList;
    }

    /**
     * 添加用户名到list中
     */
    public void addUser(String userName) {
        if (userName == null) {
            return;
        }
        int userSize = sharedPreferences.getInt(UserCons.userSize, 0);
        int currentUserNumber = userSize + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserCons.userPrefix + currentUserNumber, userName);
        editor.commit();
    }

    /**
     * 删除本地所有的userList
     */
    private void deleteAllLocalUserList() {
        int userSize = sharedPreferences.getInt(UserCons.userSize, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < userSize; i++) {
            editor.remove(UserCons.userPrefix + i);
        }
        editor.commit();
    }

    /**
     * 删除某一个userName
     *
     * @param userName
     */
    public void deleteUser(String userName) {
        //先获取所有userName的list
        List<String> userNameList = getLocalUserNameList();
        //将list中尝试删除userName
        boolean success = userNameList.remove(userName);
        //如果list的size没有变化---就不用再重复添加
        if (success) {
            deleteAllLocalUserList();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //将剩下的数据添加进去
            for (int i = 0; i < userNameList.size(); i++) {
                editor.putString(UserCons.userPrefix + i, userNameList.get(i));
            }
            editor.commit();
        }
    }
}

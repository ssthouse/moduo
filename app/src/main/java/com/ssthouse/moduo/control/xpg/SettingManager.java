/**
 * Project Name:XPGSdkV4AppBase
 * File Name:SettingManager.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:24
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ssthouse.moduo.control.xpg;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;

/**
 * SharePreference处理类.
 * <p>
 * 保存:
 * 当前登陆账户数据
 * 当前绑定设备数据
 *
 * @author Sunny Ding
 */
public class SettingManager {

    private SharedPreferences spf;

    private Context context;

    /**
     * preference文件名
     */
    private final String SHARE_PREFERENCES = "set";

    // 用户名
    private final String USER_NAME = "username";
    // 手机号码
    private final String PHONE_NUM = "phonenumber";
    // 密码
    private final String PASSWORD = "password";
    // 用户名
    private final String TOKEN = "token";
    // 用户ID
    private final String UID = "uid";

    /**
     * 当前操作设备did
     */
    private final String DID = "did";

    /**
     * 构造方法
     *
     * @param context
     */
    private SettingManager(Context context) {
        this.context = context;
        spf = context.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * 单例
     */
    private static SettingManager instance;

    /**
     * 获取单例
     *
     * @return
     */
    public static SettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingManager(context);
        }
        return instance;
    }

    /**
     * SharePreference clean.
     */
    public void clean() {
        setUid("");
        setToken("");
        setPhoneNumber("");
        setPassword("");
        setUserName("");
    }

    /**
     * 设置当前设备
     *
     * @param did
     */
    public void setCurrentDid(String did) {
        spf.edit()
                .putString(DID, did)
                .commit();
    }

    /**
     * 获取当前操作设备
     *
     * @return
     */
    public String getCurrentDid() {
        return spf.getString(DID, null);
    }

    /**
     * 保存用户登陆数据
     *
     * @param event
     */
    public void setLoginInfo(XPGLoginResultEvent event) {
        setUid(event.getUid());
        setToken(event.getToken());
    }

    /**
     * Sets the user name.
     *
     * @param name the new user name
     */
    public void setUserName(String name) {
        spf.edit().putString(USER_NAME, name).commit();
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return spf.getString(USER_NAME, "");
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        spf.edit().putString(PHONE_NUM, phoneNumber).commit();
    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return spf.getString(PHONE_NUM, "");
    }

    public void setPassword(String psw) {
        spf.edit().putString(PASSWORD, psw).commit();
    }

    public String getPassword() {
        return spf.getString(PASSWORD, "");
    }

    public void setToken(String token) {
        spf.edit().putString(TOKEN, token).commit();
    }

    public String getToken() {
        return spf.getString(TOKEN, "");
    }

    public void setUid(String uid) {
        spf.edit().putString(UID, uid).commit();
    }

    public String getUid() {
        return spf.getString(UID, "");
    }
}

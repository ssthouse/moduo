/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CmdCenter.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:19
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.ssthouse.moduo.control.xpg;

import android.content.Context;

import com.ssthouse.moduo.model.cons.Constant;
import com.ssthouse.moduo.model.cons.xpg.JsonKeys;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * ClassName: Class CmdCenter. <br/>
 * 控制指令类
 * <br/>
 * date: 2014-12-15 12:09:02 <br/>
 *
 * @author Lien
 */
public class CmdCenter {
    /**
     * The xpg wifi sdk.
     */
    private static XPGWifiSDK xpgWifiGCC;

    /**
     * The m center.
     */
    private static CmdCenter mCenter;

    /**
     * The m setting manager.
     */
    private SettingManager mSettingManager;

    /**
     * Instantiates a new cmd center.
     *
     * @param c the c
     */
    private CmdCenter(Context c) {
        if (mCenter == null) {
            init(c);
        }
    }

    /**
     * Gets the single instance of CmdCenter.
     *
     * @param c the c
     * @return single instance of CmdCenter
     */
    public static CmdCenter getInstance(Context c) {
        if (mCenter == null) {
            mCenter = new CmdCenter(c);
        }
        return mCenter;
    }

    /**
     * Inits the.
     *
     * @param c the c
     */
    private void init(Context c) {
        mSettingManager = SettingManager.getInstance(c);
        xpgWifiGCC = XPGWifiSDK.sharedInstance();
    }

    /**
     * Gets the XPG wifi sdk.
     *
     * @return the XPG wifi sdk
     */
    public XPGWifiSDK getXPGWifiSDK() {
        return xpgWifiGCC;
    }

    // =================================================================
    //
    // 关于账号的指令
    //
    // =================================================================

    /**
     * 注册账号.
     *
     * @param phone    注册手机号
     * @param code     验证码
     * @param password 注册密码
     */
    public void cRegisterPhoneUser(String phone, String code, String password) {
        xpgWifiGCC.registerUserByPhoneAndCode(phone, password, code);
    }

    /**
     * 匿名用户转正常用户
     *
     * @param token
     * @param username
     * @param password
     */
    public void cTransferToNormalUser(String token, String username, String password) {
        xpgWifiGCC.transAnonymousUserToNormalUser(token, username, password);
    }

    /**
     * C register mail user.
     *
     * @param mailAddr the mail addr
     * @param password the password
     */
    public void cRegisterMailUser(String mailAddr, String password) {
        xpgWifiGCC.registerUserByEmail(mailAddr, password);
    }

    /**
     * 注册用户
     *
     * @param userName
     * @param password
     */
    public void cRegisterUser(String userName, String password) {
        xpgWifiGCC.registerUser(userName, password);
    }

    /**
     * 匿名登录
     * <p/>
     * 如果一开始不需要直接注册账号，则需要进行匿名登录.
     */
    public void cLoginAnonymousUser() {
        xpgWifiGCC.userLoginAnonymous();
    }

    /**
     * 账号注销.
     */
    public void cLogout() {
        Timber.e("cLogout:uesrid=" + mSettingManager.getUid());
        xpgWifiGCC.userLogout(mSettingManager.getUid());
    }

    /**
     * 账号登陆.
     *
     * @param name 用户名
     * @param psw  密码
     */
    public void cLogin(String name, String psw) {
        xpgWifiGCC.userLoginWithUserName(name, psw);
    }

    /**
     * 忘记密码.
     *
     * @param phone       手机号
     * @param code        验证码
     * @param newPassword the new password
     */
    public void cChangeUserPasswordWithCode(String phone, String code, String newPassword) {
        xpgWifiGCC.changeUserPasswordByCode(phone, code, newPassword);
    }

    /**
     * 修改密码.
     *
     * @param token  令牌
     * @param oldPsw 旧密码
     * @param newPsw 新密码
     */
    public void cChangeUserPassword(String token, String oldPsw, String newPsw) {
        xpgWifiGCC.changeUserPassword(token, oldPsw, newPsw);
    }

    /**
     * 根据邮箱修改密码.
     *
     * @param email 邮箱地址
     */
    public void cChangePassworfByEmail(String email) {
        xpgWifiGCC.changeUserPasswordByEmail(email);
    }

    /**
     * 请求向手机发送验证码.
     *
     * @param phone 手机号
     */
    public void cRequestSendVerifyCode(String phone) {
        xpgWifiGCC.requestSendVerifyCode(phone);
    }

    /**
     * 发送airlink广播，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetAirLink(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeAirLink, 60);
    }

    /**
     * softap，把需要连接的wifi的ssid和password发给模块。.
     *
     * @param wifi     wifi名字
     * @param password wifi密码
     */
    public void cSetSoftAp(String wifi, String password) {
        xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP, 30);
    }

    /**
     * 绑定后刷新设备列表，该方法会同时获取本地设备以及远程设备列表.
     *
     * @param uid   用户名
     * @param token 令牌
     */
    public void cGetBoundDevices(String uid, String token) {
        //仅仅绑定自己的productkey的数据
        xpgWifiGCC.getBoundDevices(uid, token, Constant.SettingSdkCons.PRODUCT_KEY);
    }

    /**
     * 绑定设备.
     *
     * @param uid      用户名
     * @param token    密码
     * @param did      did
     * @param passcode passcode
     * @param remark   备注
     */
    public void cBindDevice(String uid, String token, String did,
                            String passcode, String remark) {

        xpgWifiGCC.bindDevice(uid, token, did, passcode, remark);
    }

    // =================================================================
    //
    // 关于控制设备的指令
    //
    // =================================================================


    /**
     * 获取设备状态.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cGetStatus(XPGWifiDevice xpgWifiDevice) {
        if (xpgWifiDevice == null) {
            Timber.e("设备为空");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", 2);
        } catch (JSONException e) {
            e.printStackTrace();
            Timber.e("json出错");
        }
        xpgWifiDevice.write(json.toString());
    }

    /**
     * 断开连接.
     *
     * @param xpgWifiDevice the xpg wifi device
     */
    public void cDisconnect(XPGWifiDevice xpgWifiDevice) {
        xpgWifiDevice.disconnect();
    }

    /**
     * 解除绑定.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     */
    public void cUnbindDevice(String uid, String token, String did,
                              String passCode) {
        xpgWifiGCC.unbindDevice(uid, token, did, passCode);
    }

    /**
     * 更新备注.
     *
     * @param uid      the uid
     * @param token    the token
     * @param did      the did
     * @param passCode the pass code
     * @param remark   the remark
     */
    public void cUpdateRemark(String uid, String token, String did,
                              String passCode, String remark) {
        xpgWifiGCC.bindDevice(uid, token, did, passCode, remark);
    }

    // =================================================================
    //
    // 设备控制相关指令
    //
    // =================================================================


    /**
     * 发送指令.
     * 抽象出主体逻辑的命令方法
     *
     * @param xpgWifiDevice the xpg wifi device
     * @param key           the key
     * @param value         the value
     */
    public void cWrite(XPGWifiDevice xpgWifiDevice, String key, Object value) {
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(key, value);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
            Timber.e("send_data:\t" + jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //开启设备的video数据点---打开video
    public void cWriteVideo(XPGWifiDevice xpgWifiDevice, int value) {
        if (value == 0) {
            cWrite(xpgWifiDevice, JsonKeys.VIDEO, false);
        } else if (value == 1) {
            cWrite(xpgWifiDevice, JsonKeys.VIDEO, true);
        }
    }

    /**
     * 改变头部
     *
     * @param xpgWifiDevice
     * @param xHead
     * @param yHead
     * @param zHead
     */
    public void cWriteHead(XPGWifiDevice xpgWifiDevice, int xHead, int yHead, int zHead) {
//        Timber.e("control moduo: △X:%d    △Y:%d    △Z:%d    ", xHead, yHead, zHead);
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(JsonKeys.X_HEAD, xHead);
            jsonParam.put(JsonKeys.Y_HEAD, yHead);
            jsonParam.put(JsonKeys.Z_HEAD, zHead);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
//            Timber.e(jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变身体
     *
     * @param xpgWifiDevice
     * @param xBody
     * @param yBody
     * @param zBody
     */
    public void cWriteBody(XPGWifiDevice xpgWifiDevice, int xBody, int yBody, int zBody) {
        //按照一定格式发送数据
        try {
            JSONObject jsonSend = new JSONObject();
            JSONObject jsonParam = new JSONObject();
            jsonSend.put("cmd", 1);
            jsonParam.put(JsonKeys.X_BODY, xBody);
            jsonParam.put(JsonKeys.Y_BODY, yBody);
            jsonParam.put(JsonKeys.Z_BODY, zBody);
            jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
            Timber.e(jsonSend.toString());
            xpgWifiDevice.write(jsonSend.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

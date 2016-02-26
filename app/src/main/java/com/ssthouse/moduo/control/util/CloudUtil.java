package com.ssthouse.moduo.control.util;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.bean.UserInfo;
import com.ssthouse.moduo.model.bean.device.Device;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * leancloud控制类
 * Created by ssthouse on 2016/1/20.
 */
public class CloudUtil {

    /**
     * 魔哆设备数据表
     */
    public static final String TABLE_MODUO_DEVICE = "ModuoDevice";

    public static final String KEY_DID = "did";
    public static final String KEY_PASSCODE = "passcode";
    public static final String KEY_CID = "cid";
    public static final String KEY_VIDEO_USERNAME = "videoUserName";
    public static final String KEY_VIDEO_PASSWORD = "videoPassword";

    /**
     * 保存设备数据到leancloud
     *
     * @param callback
     */
    public static void saveDeviceToCloud(final Device device, final SaveCallback callback) {
        Observable.just(device.getXpgWifiDevice().getDid())
                .map(new Func1<String, AVObject>() {
                    @Override
                    public AVObject call(String s) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_MODUO_DEVICE);
                        query.whereEqualTo(KEY_DID, s);
                        AVObject moduoDevice = null;
                        try {
                            moduoDevice = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return moduoDevice;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //云端没有才保存
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDevice.put(KEY_DID, device.getXpgWifiDevice().getDid());
                            moduoDevice.put(KEY_PASSCODE, device.getXpgWifiDevice().getPasscode());
                            moduoDevice.put(KEY_CID, device.getVideoCidNumber());
                            moduoDevice.put(KEY_VIDEO_USERNAME, device.getVideoUsername());
                            moduoDevice.put(KEY_VIDEO_PASSWORD, device.getVideoUsername());
                            moduoDevice.saveInBackground(callback);
                        }
                    }
                });
    }

    /**
     * 保存设备数据到leancloud
     */
    public static void saveDeviceToCloud(final ModuoInfo moduoInfo) {
        Observable.just(moduoInfo)
                .map(new Func1<ModuoInfo, AVObject>() {
                    @Override
                    public AVObject call(ModuoInfo moduo) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_MODUO_DEVICE);
                        query.whereEqualTo(KEY_DID, moduo.getDid());
                        AVObject moduoDevice = null;
                        try {
                            moduoDevice = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return moduoDevice;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //云端没有才保存
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDevice.put(KEY_DID, moduoInfo.getDid());
                            moduoDevice.put(KEY_PASSCODE, moduoInfo.getPassCode());
                            moduoDevice.put(KEY_CID, moduoInfo.getCid());
                            moduoDevice.put(KEY_VIDEO_USERNAME, moduoInfo.getVideoUsername());
                            moduoDevice.put(KEY_VIDEO_PASSWORD, moduoInfo.getVideoPassword());
                            moduoDevice.saveInBackground();
                        }
                    }
                });
    }

    /**
     * 用户信息数据表
     */
    public final static String TABLE_USER_INFO = "UserInfo";

    public final static String KEY_USERNAME = "username";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_GESTURE_PASSWORD = "gesturePassword";

    /**
     * todo
     * 从云端获取用户信息
     *
     * @param username 用户名(机智云端用户名是唯一的)
     * @return
     */
    public static void updateUserInfoToLocal(final Context context, final String username) {
        //username不能为空
        if (TextUtils.isEmpty(username)) {
            return;
        }
        Observable.just(username)
                .map(new Func1<String, UserInfo>() {
                    @Override
                    public UserInfo call(String s) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(CloudUtil.TABLE_USER_INFO);
                        query.whereEqualTo(CloudUtil.KEY_USERNAME, s);
                        AVObject userInfoObject = null;
                        try {
                            userInfoObject = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        if (userInfoObject == null) {
                            return null;
                        }
                        return new UserInfo(userInfoObject.getString(CloudUtil.KEY_USERNAME),
                                userInfoObject.getString(CloudUtil.KEY_PASSWORD),
                                userInfoObject.getString(CloudUtil.KEY_GESTURE_PASSWORD));
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserInfo>() {
                    @Override
                    public void call(UserInfo userInfo) {
                        if (userInfo == null) {
                            return;
                        }
                        SettingManager.getInstance(context).setCurrentUserInfo(userInfo);
                        Timber.e(userInfo.toString());
                        Timber.e("从云端获取用户数据---同步到本地");
                    }
                });
    }

    /**
     * 本地数据保存到云端
     * 如果username不存在---就创建新的UserObject
     *
     * @param userInfo
     */
    public static void updateUserInfoToCloud(final UserInfo userInfo) {
        //完整才上传
        if (!userInfo.isComplete()) {
            return;
        }
        Observable.just(userInfo)
                .map(new Func1<UserInfo, AVObject>() {
                    @Override
                    public AVObject call(UserInfo userInfo) {
                        //搜索云端是否存在用户
                        AVObject avObject = null;
                        try {
                            avObject = new AVQuery(TABLE_USER_INFO)
                                    .whereEqualTo(KEY_USERNAME, userInfo.getUsername())
                                    .getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return avObject;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        //不存在新建
                        if (avObject == null) {
                            AVObject newUserInfoObject = new AVObject(TABLE_USER_INFO);
                            newUserInfoObject.put(KEY_USERNAME, userInfo.getUsername());
                            newUserInfoObject.put(KEY_PASSWORD, userInfo.getPassword());
                            newUserInfoObject.put(KEY_GESTURE_PASSWORD, userInfo.getGesturePassword());
                            newUserInfoObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {

                                }
                            });
                        } else {
                            //存在更新数据
                            avObject.put(KEY_USERNAME, userInfo.getUsername());
                            avObject.put(KEY_PASSWORD, userInfo.getPassword());
                            avObject.put(KEY_GESTURE_PASSWORD, userInfo.getGesturePassword());
                            avObject.saveInBackground();
                        }
                    }
                });
    }
}

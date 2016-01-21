package com.ssthouse.moduo.main.control.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.moduo.bean.device.Device;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * leancloud控制类
 * Created by ssthouse on 2016/1/20.
 */
public class CloudUtil {

    /**
     * 魔哆设备数据表
     */
    private static final String TABLE_MODUO_DEVICE = "ModuoDevice";

    private static final String KEY_DID = "did";
    private static final String KEY_PASSCODE = "passcode";
    private static final String KEY_CID = "cid";
    private static final String KEY_VIDEO_USERNAME = "videoUserName";
    private static final String KEY_VIDEO_PASSWORD = "videoPassword";


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
                        query.whereEqualTo(KEY_DID, device.getXpgWifiDevice().getDid());
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
                        //判断远端是否已有
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDevice.put(KEY_DID, device.getXpgWifiDevice().getDid());
                            moduoDevice.put(KEY_PASSCODE, device.getXpgWifiDevice().getPasscode());
                            moduoDevice.put(KEY_CID, device.getVideoCidNumber());
                            moduoDevice.put(KEY_VIDEO_USERNAME, device.getVideoUsername());
                            moduoDevice.put(KEY_VIDEO_PASSWORD, device.getVideoUsername());
                            moduoDevice.saveInBackground(callback);
                        } else {
                            avObject.put(KEY_DID, device.getXpgWifiDevice().getDid());
                            avObject.put(KEY_PASSCODE, device.getXpgWifiDevice().getPasscode());
                            avObject.put(KEY_CID, device.getVideoCidNumber());
                            avObject.put(KEY_VIDEO_USERNAME, device.getVideoUsername());
                            avObject.put(KEY_VIDEO_PASSWORD, device.getVideoUsername());
                            avObject.saveInBackground(callback);
                        }
                    }
                });
    }

    /**
     * 用户信息数据表
     */
    private static final String TABLE_USER_INFO = "UserInfo";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_GESTURE_PASSWORD = "gesturePassword";

    /**
     * 保存用户信息
     *
     * @param username
     * @param password
     * @param gesturePassword
     */
    public static void saveUserInfoToCloud(final String username, final String password,
                                           final String gesturePassword, final SaveCallback callback) {
        Observable.just(username)
                .map(new Func1<String, AVObject>() {
                    @Override
                    public AVObject call(String s) {
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_USER_INFO);
                        query.whereEqualTo(KEY_USERNAME, username);
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
                        //判断是否已有用户
                        if (avObject == null) {
                            AVObject moduoDevice = new AVObject(TABLE_USER_INFO);
                            moduoDevice.put(KEY_USERNAME, username);
                            moduoDevice.put(KEY_PASSWORD, password);
                            moduoDevice.put(KEY_GESTURE_PASSWORD, gesturePassword);
                            moduoDevice.saveInBackground(callback);
                        } else {
                            avObject.put(KEY_USERNAME, username);
                            avObject.put(KEY_PASSWORD, password);
                            avObject.put(KEY_GESTURE_PASSWORD, gesturePassword);
                            avObject.saveInBackground(callback);
                        }
                    }
                });
    }
}

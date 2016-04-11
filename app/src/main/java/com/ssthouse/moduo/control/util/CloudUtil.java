package com.ssthouse.moduo.control.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.bean.device.Device;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * leancloud控制类{
 *     从leancloud端获取魔哆数据
 * }
 * Created by ssthouse on 2016/1/20.
 */
public class CloudUtil {

    /**
     * 魔哆设备数据表
     */
    public static final String TABLE_MODUO_DEVICE = "ModuoDevice";
    //数据列
    public static final String KEY_DID = "did";
    public static final String KEY_PASSCODE = "passcode";
    public static final String KEY_CID = "cid";
    public static final String KEY_VIDEO_USERNAME = "videoUserName";
    public static final String KEY_VIDEO_PASSWORD = "videoPassword";


    /**
     * 用户信息数据表
     */
    public final static String TABLE_USER_INFO = "UserInfo";
    //数据列
    public final static String KEY_USERNAME = "username";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_GESTURE_PASSWORD = "gesturePassword";


    /*****
     * 用户返回数据表
     */
    public final static String TABLE_ISSUE_FEEDBACK = "IssueFeedback";
    //用户联系方式
    public final static String KEY_CONTACT_INFO = "contactInfo";
    //反馈内容
    public final static String KEY_ISSUE_CONTENT = "content";


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
                            AVObject moduoDeviceObj = new AVObject(TABLE_MODUO_DEVICE);
                            moduoDeviceObj.put(KEY_DID, device.getXpgWifiDevice().getDid());
                            moduoDeviceObj.put(KEY_PASSCODE, device.getXpgWifiDevice().getPasscode());
                            moduoDeviceObj.put(KEY_CID, device.getVideoCidNumber());
                            moduoDeviceObj.put(KEY_VIDEO_USERNAME, device.getVideoUsername());
                            moduoDeviceObj.put(KEY_VIDEO_PASSWORD, device.getVideoUsername());
                            moduoDeviceObj.saveInBackground(callback);
                        }
                    }
                });
    }


    //获取云端设备数据
    public static Observable<ModuoInfo> getDeviceFromCloud(String did) {
        return Observable.just(did)
                .map(new Func1<String, ModuoInfo>() {
                    @Override
                    public ModuoInfo call(String strDid) {
                        //获取moduoObject
                        AVQuery<AVObject> query = new AVQuery<AVObject>(TABLE_MODUO_DEVICE);
                        query.whereEqualTo(KEY_DID, strDid);
                        AVObject moduoDeviceObject = null;
                        try {
                            moduoDeviceObject = query.getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                            moduoDeviceObject = null;
                        }
                        //
                        if (moduoDeviceObject == null) {
                            return null;
                        } else {
                            return new ModuoInfo(moduoDeviceObject.getString(KEY_DID),
                                    moduoDeviceObject.getString(KEY_PASSCODE),
                                    moduoDeviceObject.getString(KEY_CID),
                                    moduoDeviceObject.getString(KEY_VIDEO_USERNAME),
                                    moduoDeviceObject.getString(KEY_VIDEO_PASSWORD));
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
     * 获取指定用户名的云端UserInfo AVObject
     *
     * @param username 指定username
     * @return
     */
    public static Observable<AVObject> getUserInfoObject(final String username) {
        return Observable.just(username)
                .map(new Func1<String, AVObject>() {
                    @Override
                    public AVObject call(String s) {
                        AVObject userInfoObj = null;
                        try {
                            userInfoObj = new AVQuery(TABLE_USER_INFO)
                                    .whereEqualTo(KEY_USERNAME, username)
                                    .getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return userInfoObj;
                    }
                });
    }
}

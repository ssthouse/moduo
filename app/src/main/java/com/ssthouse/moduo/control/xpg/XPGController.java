package com.ssthouse.moduo.control.xpg;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.ssthouse.moduo.model.bean.device.Device;
import com.ssthouse.moduo.model.bean.device.DeviceData;
import com.ssthouse.moduo.model.event.account.AnonymousUserTransEvent;
import com.ssthouse.moduo.model.event.account.RegisterResultEvent;
import com.ssthouse.moduo.model.event.xpg.AuthCodeSendResultEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceDataChangedEvent;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.GetDeviceDataEvent;
import com.ssthouse.moduo.model.event.xpg.UnbindResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLogoutEvent;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceLoginEvent;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceOnLineEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xtremeprog.xpgconnect.XPGWifiSSID;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * XPG总控制器:
 * <p/>
 * 监听两个接口:
 * XPGWiﬁSDKListener通用监听器，包含了注册、登录、配置设备、绑定设备等回调接口
 * XPGWiﬁDeviceListener设备监听器，包含了单个设备的登录、控制、状态上报等接口
 * <p/>
 * Created by ssthouse on 2015/12/19.
 */
public class XPGController {

    //sdk登陆状态
    private static boolean login = false;

    //单例
    private static XPGController mInstance;
    private Context context;

    /**
     * 指令管理器.
     */
    protected CmdCenter mCenter;

    /**
     * SharePreference处理类.
     */
    protected SettingManager settingManager;

    /**
     * 当前操作的设备
     */
    protected static Device currentDevice;


    /**
     * 获取单例
     *
     * @return
     */
    public static XPGController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new XPGController(context);
        }
        return mInstance;
    }

    /**
     * 构造方法
     *
     * @param context
     */
    private XPGController(Context context) {
        this.context = context;
        //初始化监听器
        settingManager = SettingManager.getInstance(context);
        mCenter = CmdCenter.getInstance(context);
        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        mCenter.getXPGWifiSDK().setListener(sdkListener);
    }

    /**
     * XPGWifiDeviceListener
     * <p/>
     * 设备属性监听器。 设备连接断开、获取绑定参数、获取设备信息、控制和接受设备信息相关.
     */
    protected XPGWifiDeviceListener deviceListener = new XPGWifiDeviceListener() {

        @Override
        public void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {
            Timber.e("设备上线");
            EventBus.getDefault().post(new XpgDeviceOnLineEvent(device.getDid(), isOnline));
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            Timber.e("设备连接断开");
            //todo---这个是在什么时候触发的??
//            EventBus.getDefault().post(new XpgDeviceLoginEvent());
        }

        @Override
        public void didLogin(XPGWifiDevice device, int result) {
            Timber.e("设备登陆:\t" + device.getDid());
            if (result == 0) {
                EventBus.getDefault().post(new XpgDeviceLoginEvent(true, device.getDid()));
                Timber.e(device.getDid() + "登陆成功");
            } else {
                EventBus.getDefault().post(new XpgDeviceLoginEvent(false, device.getDid()));
                Timber.e(device.getDid() + "登陆失败");
            }
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            if (dataMap == null || dataMap.get("data") == null) {
                Timber.e("收到设备空的消息, errorCode:\t" + result);
                EventBus.getDefault().post(new GetDeviceDataEvent(false));
                return;
            }
            //普通数据点类型，有布尔型、整形和枚举型数据，该种类型一般为可读写
            if (dataMap.get("data") != null) {
                //解析json数据
                JsonParser parser = new JsonParser();
                JsonObject jsonData = (JsonObject) parser.parse("" + dataMap.get("data"));
                JsonElement cmdElement = jsonData.get(DeviceData.DeviceCons.CMD);

                //得到事件类型---设备数据
                int cmd = cmdElement.getAsInt();
                DeviceData deviceData = DeviceData.getDeviceData(device, dataMap);
                //发出事件
                if (cmd == 3) {
                    EventBus.getDefault().post(new GetDeviceDataEvent(true, deviceData));
                    Timber.e("获取设备数据回调");
                } else if (cmd == 4) {
                    EventBus.getDefault().post(new DeviceDataChangedEvent(deviceData));
                    Timber.e("设备主动推送数据变化");
                }
            }
            //设备报警数据点类型，该种数据点只读，设备发生报警后该字段有内容，没有发生报警则没内容
            if (dataMap.get("alters") != null) {
                Timber.e("alert:\t" + dataMap.get("alters"));
            }
            //设备错误数据点类型，该种数据点只读，设备发生错误后该字段有内容，没有发生报警则没内容
            if (dataMap.get("faults") != null) {
                Timber.e("alert:\t" + dataMap.get("faults"));
            }
        }
    };

    /**
     * XPGWifiSDKListener
     * <p/>
     * sdk监听器。 配置设备上线、注册登录用户、搜索发现设备、用户绑定和解绑设备相关.
     */
    private XPGWifiSDKListener sdkListener = new XPGWifiSDKListener() {

        @Override
        public void didBindDevice(int error, String errorMessage, String did) {
            //绑定设备回调
            Timber.e("绑定设备回调. 设备id:\t" + did);
            if (error == 0) {
                EventBus.getDefault().post(new DeviceBindResultEvent(true, did));
            } else {
                EventBus.getDefault().post(new DeviceBindResultEvent(false, null));
            }
        }

        @Override
        public void didChangeUserEmail(int error, String errorMessage) {
            // 修改邮箱
            Timber.e("修改邮箱");
        }

        @Override
        public void didChangeUserPassword(int error, String errorMessage) {
            //改密码
            Timber.e("改密码");
        }

        @Override
        public void didChangeUserPhone(int error, String errorMessage) {
            //改手机号
            Timber.e("改手机号");
        }

        @Override
        public void didDiscovered(int error, List<XPGWifiDevice> devicesList) {
            //发现设备
            if (error == 0) {
                EventBus.getDefault().post(new GetBoundDeviceEvent(true, devicesList));
                Timber.e("获取账号绑定设备成功");
                Timber.e("设备数目为:\t" + devicesList.size());
            } else {
                EventBus.getDefault().post(new GetBoundDeviceEvent(false));
                Timber.e("获取账号绑定设备失败");
            }
        }

        @Override
        public void didGetSSIDList(int error, List<XPGWifiSSID> ssidInfoList) {
            //路由器名字
            Timber.e("路由器名字");
        }

        @Override
        public void didRegisterUser(int error, String errorMessage, String uid,
                                    String token) {
            //注册用户完成
            if (error == 0) {
                EventBus.getDefault().post(new RegisterResultEvent(true, uid, token));
                Timber.e("注册用户完成");
            } else {
                EventBus.getDefault().post(new RegisterResultEvent(false, error));
                Timber.e("注册用户失败: " + error + "\t" + errorMessage);
            }
        }

        @Override
        public void didRequestSendVerifyCode(int error, String errorMessage) {
            //发送手机验证码回调
            if (error == 0) {
                Timber.e("验证码发送成功");
                EventBus.getDefault().post(new AuthCodeSendResultEvent(true));
            } else {
                EventBus.getDefault().post(new AuthCodeSendResultEvent(false));
            }
            Timber.e("发送手机验证码回调:\t" + error + "\t" + errorMessage);
        }

        @Override
        public void didSetDeviceWifi(int error, XPGWifiDevice device) {
            //设置设备wifi
            Timber.e("设置设备wifi");
        }

        @Override
        public void didUnbindDevice(int error, String errorMessage, String did) {
            if (error == 0) {
                EventBus.getDefault().post(new UnbindResultEvent(true, did));
            } else {
                EventBus.getDefault().post(new UnbindResultEvent(false, did));
            }
            Timber.e("解绑设备回调");
        }

        @Override
        public void didUserLogin(int error, String errorMessage, String uid,
                                 String token) {
            //用户登陆回调
            if (error == 0) {
                setLogin(true);
                EventBus.getDefault().post(new XPGLoginResultEvent(true, uid, token));
            } else {
                setLogin(false);
                EventBus.getDefault().post(new XPGLoginResultEvent(false));
            }
            Timber.e("用户登陆回调:\t" + error + "\t" + errorMessage);
        }

        @Override
        public void didUserLogout(int error, String errorMessage) {
            //用户登出回调
            Timber.e("用户登出回调");
            if (error == 0) {
                setLogin(false);
                EventBus.getDefault().post(new XPGLogoutEvent(true, error));
            } else {
                EventBus.getDefault().post(new XPGLogoutEvent(false, error));
            }
        }

        @Override
        public void didTransUser(int error, String errorMessage) {
            if (error == 0) {
                EventBus.getDefault().post(new AnonymousUserTransEvent(true, 0));
            } else {
                EventBus.getDefault().post(new AnonymousUserTransEvent(false, error));
            }
            super.didTransUser(error, errorMessage);
        }
    };


    //getter---and---setter----------------------------------------------------------
    public CmdCenter getmCenter() {
        return mCenter;
    }


    public static Device getCurrentDevice() {
        return currentDevice;
    }

    public XPGWifiDeviceListener getDeviceListener() {
        return deviceListener;
    }

    public static void setCurrentDevice(Device currentDevice) {
        XPGController.currentDevice = currentDevice;
    }

    /**
     * 将找到的xpgWifiDevice设置为当前设备
     *
     * @param context
     * @param xpgWifiDevice
     */
    public static void setCurrentDevice(final Context context, final XPGWifiDevice xpgWifiDevice) {
        Observable.just(xpgWifiDevice.getDid())
                .map(new Func1<String, AVObject>() {
                    @Override
                    public AVObject call(String s) {
                        AVObject avObject = null;
                        try {
                            avObject = new AVQuery<AVObject>(CloudUtil.TABLE_MODUO_DEVICE)
                                    .whereEqualTo(CloudUtil.KEY_DID, s)
                                    .getFirst();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                        return avObject;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        if (avObject == null) {
                            return;
                        }
                        //设置当前设备参数
                        ModuoInfo moduoInfo = new ModuoInfo(avObject.getString(CloudUtil.KEY_DID),
                                avObject.getString(CloudUtil.KEY_PASSCODE),
                                avObject.getString(CloudUtil.KEY_CID),
                                avObject.getString(CloudUtil.KEY_VIDEO_USERNAME),
                                avObject.getString(CloudUtil.KEY_VIDEO_PASSWORD));
                        SettingManager.getInstance(context).setCurrentModuoInfo(moduoInfo);
                        //设置为当前设备
                        XPGController.setCurrentDevice(new Device(xpgWifiDevice, moduoInfo));
                        //设置监听器
                        XPGController.refreshCurrentDeviceListener(context);
                        //登陆视频sdk
                        Communication.getInstance(context)
                                .addStreamer(XPGController.getCurrentDevice());
                        Timber.e("从云端更新设备Info" + moduoInfo.toString());
                    }
                });
    }

    /**
     * 刷新当前设备的listener
     *
     * @param context
     */
    public static void refreshCurrentDeviceListener(Context context) {
        if (getCurrentDevice() == null) {
            return;
        }
        //设置监听器
        getCurrentDevice().getXpgWifiDevice()
                .setListener(XPGController.getInstance(context).getDeviceListener());
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        XPGController.login = login;
    }
}

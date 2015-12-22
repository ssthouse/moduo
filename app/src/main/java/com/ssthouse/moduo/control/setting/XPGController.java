package com.ssthouse.moduo.control.setting;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ssthouse.moduo.model.DeviceData;
import com.ssthouse.moduo.model.event.setting.AuthCodeSendResultEvent;
import com.ssthouse.moduo.model.event.setting.DeviceBindResultEvent;
import com.ssthouse.moduo.model.event.setting.DeviceDataChangedEvent;
import com.ssthouse.moduo.model.event.setting.DeviceStateEvent;
import com.ssthouse.moduo.model.event.setting.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.setting.GetDeviceDataEvent;
import com.ssthouse.moduo.model.event.setting.RegisterResultEvent;
import com.ssthouse.moduo.model.event.setting.XPGLoginResultEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xtremeprog.xpgconnect.XPGWifiSSID;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.greenrobot.event.EventBus;
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

    //唯一单例
    private static XPGController instance;

    private Context context;

    /**
     * 获取单例
     *
     * @return
     */
    public static XPGController getInstance(Context context) {
        if (instance == null) {
            instance = new XPGController(context);
        }
        return instance;
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
     * 指令管理器.
     */
    protected CmdCenter mCenter;

    /**
     * SharePreference处理类.
     */
    protected SettingManager settingManager;

    /**
     * 设备列表.
     */
    protected static List<XPGWifiDevice> deviceslist = new ArrayList<XPGWifiDevice>();

    /**
     * 绑定列表
     */
    protected static List<XPGWifiDevice> bindlist = new ArrayList<XPGWifiDevice>();

    /**
     * 当前操作的设备
     */
    protected static XPGWifiDevice currentXpgWifiDevice;

    /**
     * XPGWifiDeviceListener
     * <p/>
     * 设备属性监听器。 设备连接断开、获取绑定参数、获取设备信息、控制和接受设备信息相关.
     */
    protected XPGWifiDeviceListener deviceListener = new XPGWifiDeviceListener() {

        @Override
        public void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {
            Timber.e("设备上线");
            EventBus.getDefault().post(new DeviceStateEvent());
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            Timber.e("设备连接断开");
            EventBus.getDefault().post(new DeviceStateEvent());
        }

        @Override
        public void didLogin(XPGWifiDevice device, int result) {
            Timber.e("设备登陆");
            EventBus.getDefault().post(new DeviceStateEvent());
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            if (dataMap == null) {
                Timber.e("收到设备空的消息, errorCode:\t" + result);
                return;
            }
            //普通数据点类型，有布尔型、整形和枚举型数据，该种类型一般为可读写
            if (dataMap.get("data") != null) {
                //解析json数据
                JsonParser parser = new JsonParser();
                JsonObject jsonData = (JsonObject) parser.parse("" + dataMap.get("data"));
                JsonElement dataElement = jsonData.get("entity0");

                //todo---查看数据
                Timber.e("entity0:\t" + dataElement.toString());
                JsonElement cmdElement = jsonData.get(DeviceData.DeviceCons.KEY_CMD);
                //得到---cmd---温度---湿度
                int cmd = cmdElement.getAsInt();
                int temperature = dataElement.getAsJsonObject().get(DeviceData.DeviceCons.KEY_TEMPERATURE).getAsInt();
                int humidity = dataElement.getAsJsonObject().get(DeviceData.DeviceCons.KEY_HUMIDITY).getAsInt();
                String ctrlData = dataElement.getAsJsonObject().get(DeviceData.DeviceCons.KEY_CTRL_CMD).getAsString();
                Timber.e("ctrl_cmd:\t" + ctrlData);
                Timber.e("cmd:\t" + cmd);
                Timber.e("温度" + temperature);
                Timber.e("湿度" + humidity);
                //发出事件
                if (cmd == 3) {
                    EventBus.getDefault().post(new GetDeviceDataEvent(true, new DeviceData(temperature, humidity)));
                } else if (cmd == 4) {
                    EventBus.getDefault().post(new DeviceDataChangedEvent(new DeviceData(temperature, humidity)));
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
            Timber.e("获取到设备数据");

            //// TODO: 2015/12/22
            //二进制数据点类型，适合开发者自行解析二进制数据
            if (dataMap.get("binary") != null) {
                Timber.e("Binary data:" + dataMap.get("binary"));
                //收到后自行解析
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
            //TODO---尝试登陆设备---进行控制
            Timber.e("绑定设备回调");
            if (error == 0) {
                EventBus.getDefault().post(new DeviceBindResultEvent(true));
            } else {
                EventBus.getDefault().post(new DeviceBindResultEvent(false));
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
                EventBus.getDefault().post(new RegisterResultEvent(false));
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
            Timber.e("绑定设备回调");
        }

        @Override
        public void didUserLogin(int error, String errorMessage, String uid,
                                 String token) {
            //用户登陆回调
            if (error == 0) {
                EventBus.getDefault().post(new XPGLoginResultEvent(true, uid, token));
            } else {
                EventBus.getDefault().post(new XPGLoginResultEvent(false));
            }
            Timber.e("用户登陆回调:\t" + error + "\t" + errorMessage);
        }

        @Override
        public void didUserLogout(int error, String errorMessage) {
            //用户登出回调
            Timber.e("用户登出回调");
        }
    };


    //getter---and---setter----------------------------------------------------------
    public CmdCenter getmCenter() {
        return mCenter;
    }

    public static List<XPGWifiDevice> getDeviceslist() {
        return deviceslist;
    }

    public static List<XPGWifiDevice> getBindlist() {
        return bindlist;
    }

    public static XPGWifiDevice getCurrentXpgWifiDevice() {
        return currentXpgWifiDevice;
    }

    public XPGWifiDeviceListener getDeviceListener() {
        return deviceListener;
    }

    public void setDeviceListener(XPGWifiDeviceListener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public static void setCurrentXpgWifiDevice(XPGWifiDevice currentXpgWifiDevice) {
        XPGController.currentXpgWifiDevice = currentXpgWifiDevice;
    }
}

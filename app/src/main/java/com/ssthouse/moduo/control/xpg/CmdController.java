package com.ssthouse.moduo.control.xpg;

import android.util.Base64;

import com.ssthouse.moduo.model.cons.xpg.JsonKeys;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * 命令发送控制类
 * Created by ssthouse on 2016/3/8.
 */
public class CmdController {

    private static CmdController instance;

    private CmdController() {

    }

    public static CmdController getInstance() {
        if (instance == null) {
            instance = new CmdController();
        }
        return instance;
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

    //发送控制命令
    public void cWriteCmd(XPGWifiDevice xpgWifiDevice, byte device, byte no, byte param, byte value) {
        byte data[] = new byte[4];
        data[0] = device;
        data[1] = no;
        data[2] = param;
        data[3] = value;
        String cmdStr = Base64.encodeToString(data, Base64.NO_CLOSE);
        Timber.e("cmdStr: " + cmdStr);
        cWrite(xpgWifiDevice, JsonKeys.CTRL_CMD, cmdStr);
    }

    //发送cmd_ctrl命令
    public void cWriteCmdCtrl(XPGWifiDevice xpgWifiDevice, CmdBean cmdBean) {
        cWrite(xpgWifiDevice, JsonKeys.CTRL_CMD, cmdBean.getValueStr());
    }

    //todo---测试在这里发送数据
    public void testCmd() {
        byte data[] = {9, 9, 9, 9};
        CmdController.getInstance().cWriteCmdCtrl(
                XPGController.getCurrentDevice().getXpgWifiDevice(), CmdBean.getInstance(data[0], data[1], data[2], data[3])
        );
    }
}

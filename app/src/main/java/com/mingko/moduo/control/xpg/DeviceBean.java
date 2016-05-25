package com.mingko.moduo.control.xpg;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mingko.moduo.control.xpg.Slots.AcSlots;
import com.mingko.moduo.control.xpg.Slots.LightSlots;
import com.mingko.moduo.control.xpg.Slots.SlotSlots;
import com.mingko.moduo.control.xpg.Slots.SlotsEntity;
import com.mingko.moduo.control.xpg.Slots.SwitchSlots;
import com.mingko.moduo.control.xpg.Slots.TvSlots;

import java.util.Map;

import timber.log.Timber;

/**
 * 用于第一次解析 json字符串
 * Created by SunsetKnight on 2016/5/18.
 */
public class DeviceBean {

    //设备类型 device
    interface Device {
        String TV = "tv";
        String AIRCONTROL = "airControl";
        String LIGHT = "light";
        String SWITCH = "switch";
        String SLOT = "slot";
    }

    //语义服务类型 service
    interface Service {
        String SMART_HOME = "smartHome";
    }

    //操作参数 device
    interface Operation {
        String OPEN = "OPEN";
        String CLOSE = "CLOSE";
        String SET = "SET";
    }

    private SemanticEntity semantic;
    private int rc;
    private String device;
    private String service;
    private String operation;
    private String text;

    //生成CmdBean---用于发送指令
    public CmdBean generateCmdBean() {
        SlotsEntity slots = null;
        byte deviceType, deviceNumber, param, value = 0;
        try {
            //判断语义服务类型
            switch (service) {
                case Service.SMART_HOME:
                    break;
                default:
                    return null;
            }
            //判断设备类型
            switch (device) {
                case Device.TV:
                    deviceType = CmdBean.DeviceType.TV.value;
                    slots = new Gson().fromJson(semantic.getSlots().toString(), TvSlots.class);
                    break;
                case Device.AIRCONTROL:
                    deviceType = CmdBean.DeviceType.AC.value;
                    slots = new Gson().fromJson(semantic.getSlots().toString(), AcSlots.class);
                    break;
                case Device.LIGHT:
                    deviceType = CmdBean.DeviceType.LIGHT.value;
                    slots = new Gson().fromJson(semantic.getSlots().toString(), LightSlots.class);
                    break;
                case Device.SWITCH:
                    deviceType = CmdBean.DeviceType.SWITCH.value;
                    slots = new Gson().fromJson(semantic.getSlots().toString(), SwitchSlots.class);
                    break;
                case Device.SLOT:
                    deviceType = CmdBean.DeviceType.SLOT.value;
                    slots = new Gson().fromJson(semantic.getSlots().toString(), SlotSlots.class);
                    break;
                default:
                    deviceType = CmdBean.DeviceType.NONE.value;
                    return null;
            }
            //初始化slots参数
            //slots = new Gson().fromJson(semantic.getSlots().toString(), SlotsEntity.class);
            //设备编号
            //// TODO: 2016/5/18 设备编号暂时定义为0
            deviceNumber = CmdBean.DeviceNumber.DEFULT.value;
            //参数param
            param = slots.getParam();
            value = slots.getValue();
            Timber.e("发送指令：deviceType="+deviceType+" deviceNumber="+deviceNumber+" param="+param+" value="+value);
            //返回解析出的CmdBean
            return CmdBean.getInstance(deviceType, deviceNumber, param, value);
        } catch (Exception e) {
            return null;
        }
    }

    private class SemanticEntity {
        private JsonObject slots;

        public JsonObject getSlots() {
            return slots;
        }
    }

    //---getter-------------------------------

    public String getText() {
        return text;
    }
}

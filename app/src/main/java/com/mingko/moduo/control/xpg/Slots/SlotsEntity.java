package com.mingko.moduo.control.xpg.Slots;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public abstract class SlotsEntity {

    //智能家居 Slots 参数中共有的属性
    public String deviceInstance;
    public String location;
    public String datetime;
    public String onOff;
    public String modifier;
    public byte param = 0 ;
    public byte value = 0;

    /**用于加载默认的Param值*/
    public static Map<String, Byte> mapParam = new HashMap<String, Byte>();
    public static byte byteParam = 0;
    /**用于加载默认的Value值*/
    public static Map<String, Byte> mapValue = new HashMap<String, Byte>();
    public static byte byteValue = 0;

    static {
        //初始化 Param
        mapParam.put("NONE",byteParam++);
        mapParam.put("onOff",byteParam++);//#1
        //初始化 Value
        mapValue.put("none",byteValue++);
        mapValue.put("1",byteValue++);
        mapValue.put("2",byteValue++);
        mapValue.put("3",byteValue++);
        mapValue.put("4",byteValue++);
        mapValue.put("5",byteValue++);
        mapValue.put("6",byteValue++);
        mapValue.put("7",byteValue++);
        mapValue.put("8",byteValue++);
        mapValue.put("9",byteValue++);
        mapValue.put("10",byteValue++);
        mapValue.put("OPEN",byteValue++);
        mapValue.put("CLOSE",byteValue++);
        mapValue.put("+",byteValue++);
        mapValue.put("-",byteValue++);//#14
    }

    //---getter-------------------------------------------------------
    public String getDeviceInstance() {
        return deviceInstance;
    }

    public String getLocation() {
        return location;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getOnOff() {
        return onOff;
    }

    public String getModifier() {
        return modifier;
    }

    public byte getParam(){
        initParamValue();
        if(onOff != null && !onOff.isEmpty()){
            param = mapParam.get("onOff");
            value = calValue(onOff);
        }
        return param;
    }

    public byte getValue(){
        return value;
    }

    //用于子类加载参数和值
    public abstract void initParamValue();

    public byte calValue(String key){
        return mapValue.get(key)==null?mapValue.get("none"):mapValue.get(key);
    }

}

package com.mingko.moduo.control.xpg.Slots;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public abstract class SlotsEntity {

    //智能家居 Slots 参数中共有的属性
    public String deviceInstance;
    public Object location;
    public Object datetime;
    public String onOff;
    public Object modifier;
    public byte param = 0;
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

    /**
     * 用于子类加载自定义参数和值
     */
    public abstract void initParamValue();

    public byte getParam(){
        initParamValue();
        setParamAndValue("onOff", onOff);
        return param;
    }

    public byte getValue(){
        return value;
    }

    /**
     * 设置由Json解析后不确定的 Object对象
     * 不确定的对象可能是String，可能是Map。
     * eg:
     *    {"page":"1"}
     *    {"page":{"direct":"-"}}
     * paramKey = "page";
     * valueKey = "direct";
     * valueObject = page;
     *
     * @param paramKey param的键
     * @param valueKey value的键
     * @param valueObject value所在的json对象
     */
    public void setParamAndValue(String paramKey, String valueKey, Object valueObject){
         if( valueObject instanceof String){
             setParamAndValue(paramKey, (String) valueObject);
         }else if(valueObject instanceof Map){
             Map<String, String> valueMap = (Map<String, String>) valueObject;
             setParamAndValue(paramKey,valueMap.get(valueKey));
         }
    }

    /**
     * 设置需要传递的param 和 value
     *
     * @param paramKey param的键
     * @param valueKey value的键
     */
    public void setParamAndValue(String paramKey, String valueKey) {
        if (paramKey != null && valueKey != null) {
            param = mapParam.get(paramKey);
            value = mapValue.get(valueKey) == null ? mapValue.get("none") : mapValue.get(valueKey);
        }
    }
}

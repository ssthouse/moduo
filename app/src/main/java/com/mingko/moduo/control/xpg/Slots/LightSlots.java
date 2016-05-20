package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class LightSlots extends SlotsEntity {

    private String bright;
    private String color;
    private String colorTemperature;
    private String mode;
    private String attrValue;
    private String arrrType;
    private String attr;

    static {
        byte bParam = byteParam;
        mapParam.put("attrValue", bParam++);//#2
        mapParam.put("arrrType", bParam++);
        mapParam.put("attr", bParam++);
        byte bValue = byteValue;
        mapValue.put("开", bValue++);//#15
        mapValue.put("关", bValue++);
        mapValue.put("红", bValue++);
        mapValue.put("橙", bValue++);
        mapValue.put("黄", bValue++);
        mapValue.put("绿", bValue++);
        mapValue.put("青", bValue++);
        mapValue.put("蓝", bValue++);
        mapValue.put("紫", bValue++);
        mapValue.put("String", bValue++);
        mapValue.put("int", bValue++);
        mapValue.put("开关", bValue++);
        mapValue.put("颜色", bValue++);
    }

    @Override
    public void initParamValue() {
        if(attrValue != null && !attrValue.isEmpty()){
            param = mapParam.get("attrValue");
            value = calValue(attrValue);
        }else if(arrrType != null && !arrrType.isEmpty()){
            param = mapParam.get("arrrType");
            value = calValue(arrrType);
        }else if(attr != null && !attr.isEmpty()){
            param = mapParam.get("attr");
            value = calValue(attr);
        }
    }

    public String getBright() {
        return bright;
    }

    public String getColor() {
        return color;
    }

    public String getColorTemperature() {
        return colorTemperature;
    }

    public String getMode() {
        return mode;
    }
}

package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class SwitchSlots extends SlotsEntity{

    private String mode;

    static {
        byte bParam = byteParam;
        mapParam.put("mode", bParam++);//#2
        byte bValue = byteValue;
        mapValue.put("模式一", bValue++);//#15
        mapValue.put("模式二", bValue++);
        mapValue.put("模式三", bValue++);
    }

    @Override
    public void initParamValue() {
        if(mode != null && !mode.isEmpty()){
            param = mapParam.get("mode");
            value = calValue(mode);
        }
    }

    public String getMode() {
        return mode;
    }
}

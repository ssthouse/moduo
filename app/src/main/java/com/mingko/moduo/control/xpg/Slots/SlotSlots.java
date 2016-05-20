package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class SlotSlots extends SlotsEntity{

    private String duration;
    private String mode;

    static {
        byte bParam = byteParam;
        //mapParam.put("", bParam++);//#2

        byte bValue = byteValue;
        //mapValue.put("", bValue++);//#15

    }

    @Override
    public void initParamValue() {

    }

    public String getDuration() {
        return duration;
    }

    public String getMode() {
        return mode;
    }
}

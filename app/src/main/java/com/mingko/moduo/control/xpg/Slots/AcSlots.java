package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class AcSlots extends SlotsEntity{

    private String attr;
    private String arrrType;
    private Object attrValue;

    static {
        byte bParam = byteParam;
        mapValue.put("开关", bParam++);
        mapValue.put("温度", bParam++);
        mapValue.put("制冷", bParam++);
        mapValue.put("制热", bParam++);
        mapValue.put("上下居中", bParam++);
        mapValue.put("风速", bParam++);
        mapValue.put("送风角度", bParam++);
        byte bValue = byteValue;
        mapValue.put("16", bValue++);//#15
        mapValue.put("17", bValue++);
        mapValue.put("18", bValue++);
        mapValue.put("19", bValue++);
        mapValue.put("20", bValue++);
        mapValue.put("21", bValue++);
        mapValue.put("22", bValue++);
        mapValue.put("23", bValue++);
        mapValue.put("24", bValue++);
        mapValue.put("25", bValue++);
        mapValue.put("26", bValue++);
        mapValue.put("27", bValue++);
        mapValue.put("28", bValue++);
        mapValue.put("29", bValue++);
        mapValue.put("30", bValue++);
        mapValue.put("31", bValue++);
        mapValue.put("32", bValue++);
        mapValue.put("开", bValue++);
        mapValue.put("关", bValue++);
    }

    @Override
    public void initParamValue() {
        setObject(attrValue, attr, "direct");
    }



}

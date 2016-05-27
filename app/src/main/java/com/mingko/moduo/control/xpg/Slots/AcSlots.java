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
        mapParam.put("开关", bParam++);
        mapParam.put("温度", bParam++);
        mapParam.put("制冷", bParam++);
        mapParam.put("制热", bParam++);
        mapParam.put("上下居中", bParam++);
        mapParam.put("上下扫风", bParam++);
        mapParam.put("左右扫风", bParam++);
        mapParam.put("风速", bParam++);
        mapParam.put("送风角度", bParam++);
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
        setParamAndValue(attr, "direct", attrValue);
    }

}

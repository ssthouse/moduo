package com.mingko.moduo.control.xpg.Slots;

/**
 * Created by SunsetKnight on 2016/5/18.
 */
public class AcSlots extends SlotsEntity{

    private String duration;
    private String mode;
    private String temperature;
    private String fanSpeed;
    private String airflowDirection;
    private String airflowAngel;
    private String backgroundLightSwitch;
    private String electricAuxiliarySwitch;
    private String powerfulSwitch;
    private String mildewProofSwitch;
    private String ecoSwitch;
    private String queried;
    private String attrValue;
    private String arrrType;
    private String attr;

    static {
        byte bParam = byteParam;
        mapParam.put("attrValue", bParam++);//#2
        mapParam.put("arrrType", bParam++);
        mapParam.put("attr", bParam++);
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
        mapValue.put("String", bValue++);
        mapValue.put("int", bValue++);
        mapValue.put("开关", bValue++);
        mapValue.put("温度", bValue++);
        mapValue.put("制冷", bValue++);
        mapValue.put("制热", bValue++);
        mapValue.put("上下居中", bValue++);
        mapValue.put("风速", bValue++);
        mapValue.put("送风角度", bValue++);
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

    public String getDuration() {
        return duration;
    }

    public String getMode() {
        return mode;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getFanSpeed() {
        return fanSpeed;
    }

    public String getAirflowDirection() {
        return airflowDirection;
    }

    public String getAirflowAngel() {
        return airflowAngel;
    }

    public String getBackgroundLightSwitch() {
        return backgroundLightSwitch;
    }

    public String getElectricAuxiliarySwitch() {
        return electricAuxiliarySwitch;
    }

    public String getPowerfulSwitch() {
        return powerfulSwitch;
    }

    public String getMildewProofSwitch() {
        return mildewProofSwitch;
    }

    public String getEcoSwitch() {
        return ecoSwitch;
    }

    public String getQueried() {
        return queried;
    }
}

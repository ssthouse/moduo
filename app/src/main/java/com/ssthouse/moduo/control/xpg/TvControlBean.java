package com.ssthouse.moduo.control.xpg;

import static com.ssthouse.moduo.control.xpg.CmdBean.DeviceParam;
import static com.ssthouse.moduo.control.xpg.CmdBean.DeviceType;

/**
 * 电视控制命令bean
 * Created by ssthouse on 2016/3/17.
 */
public class TvControlBean {


    //语义服务类型
    interface Service {
        String SMART_HOME = "smartHome";
    }

    //设备类型
    interface Device {
        String TV = "tv";
    }

    //操作参数param
    interface Param {
        String OPEN = "OPEN";
        String CLOSE = "CLOSE";
        String SET = "SET";
    }

    //参数值value
    interface Value {
        String OPEN = "OPEN";
        String CLOSE = "CLOSE";

        //音量方向
        String VOLUME_PLUS = "+";
        String VOLUME_MINUS = "-";
    }

    /**
     * slots : {"volume":{"direct":"-"}}
     */

    private SemanticEntity semantic;
    /**
     * semantic : {"slots":{"volume":{"direct":"-"}}}
     * rc : 0
     * device : tv
     * service : smartHome
     * operation : SET
     * text : 电视音量调小。
     */

    private int rc;
    private String device;
    private String service;
    private String operation;
    private String text;

    public void setSemantic(SemanticEntity semantic) {
        this.semantic = semantic;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SemanticEntity getSemantic() {
        return semantic;
    }

    public int getRc() {
        return rc;
    }

    public String getDevice() {
        return device;
    }

    public String getService() {
        return service;
    }

    public String getOperation() {
        return operation;
    }

    public String getText() {
        return text;
    }

    public static class SemanticEntity {
        /**
         * volume : {"direct":"-"}
         */

        private SlotsEntity slots;

        public void setSlots(SlotsEntity slots) {
            this.slots = slots;
        }

        public SlotsEntity getSlots() {
            return slots;
        }

        public static class SlotsEntity {


            //开关Entity
            private String onOff;

            public String getOnOff() {
                return onOff;
            }

            public void setOnOff(String onOff) {
                this.onOff = onOff;
            }

            //音量Entity
            /**
             * direct : -
             */

            private VolumeEntity volume;

            public void setVolume(VolumeEntity volume) {
                this.volume = volume;
            }

            public VolumeEntity getVolume() {
                return volume;
            }

            public static class VolumeEntity {
                private String direct;

                public void setDirect(String direct) {
                    this.direct = direct;
                }

                public String getDirect() {
                    return direct;
                }
            }
        }
    }

    //生成CmdBean---用于发送指令
    public CmdBean generateCmdBean() {
        try {
            byte deviceType, deviceNumber, param, value = 0;
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
                    deviceType = DeviceType.TV.value;
                    break;
                default:
                    //todo---暂时只处理TV
                    return null;
                //deviceType = CmdBean.DeviceType.NONE.value;
            }
            //设备编号
            deviceNumber = (byte) rc;
            //参数param
            switch (operation) {
                //电视开关
                case Param.OPEN:
                case Param.CLOSE:
                    param = DeviceParam.ONOFF.value;
                    //获取参数value
                    switch (semantic.getSlots().getOnOff()) {
                        case Value.OPEN:
                            value = 1;
                            break;
                        case Value.CLOSE:
                            value = 2;
                            break;
                        default:
                            value = 0;
                            break;
                    }
                    break;
                //电视音量
                case Param.SET:
                    param = DeviceParam.TV_VOLUME.value;
                    switch ((semantic.getSlots().getVolume().getDirect())) {
                        case Value.VOLUME_PLUS:
                            value = 1;
                            break;
                        case Value.VOLUME_MINUS:
                            value = 2;
                            break;
                        default:
                            value = 0;
                            break;
                    }
                    break;
                default:
                    return null;
            }
            //返回解析出的CmdBean
            return CmdBean.getInstance(deviceType, deviceNumber, param, value);
        } catch (Exception e) {
            return null;
        }
    }
}

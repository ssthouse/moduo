package com.ssthouse.moduo.fragment.moduo.bean;

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
    interface Device{
        String TV = "tv";
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

    //todo---生成CmdBean
//    public CmdBean generateCmdBean() {
//        //判断语义服务类型
//        switch (service) {
//            case Service.SMART_HOME:
//                break;
//            default:
//                return null;
//            break;
//        }
//        //判断设备类型
//        switch (device) {
//            case Device.TV:
//
//                break;
//        }
//    }


    @Override
    public String toString() {
        //todo---将其转化为回复给用户的指令语言
        return "正在开发中...";
    }
}

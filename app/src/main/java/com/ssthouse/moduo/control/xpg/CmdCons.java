package com.ssthouse.moduo.control.xpg;

/**
 * 命令操作常量
 * Created by ssthouse on 2016/3/3.
 */
public class CmdCons {

    //数据byte长度
    public static final int COMMAND_SIZE = 4;
    public static final int PARAMETER_SIZE = 4;

    //设备类型
    enum DeviceType {
        APP_NONE,
        APP_TV,
        APP_AC,
        APP_END
    }

    //设备控制属性
    enum DeviceParam {
       PARAM_None,
       PARAM_TV_ONOFF,
       PARAM_TV_VOLUME,
       PARAM_TV_CHANNEL,
       PARAM_TV_SRC,
       PARAM_AC_ONOFF,
       PARAM_AC_TEMP,
       PARAM_AC_MODE,
       PARAM_PARAM_END
    }
}

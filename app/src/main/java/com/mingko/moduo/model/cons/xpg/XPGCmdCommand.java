package com.mingko.moduo.model.cons.xpg;

/**
 * 机智云 Cmd 命令映射表
 *
 */
public enum XPGCmdCommand{

    /** 向设备发送控制命令 */
    SEND_COMMAND_TO_DEVICE(1),

    /** 向设备查询状态 */
    QUERY_STATUE_FROM_DEVICE(2),

    /** 设备返回当前状态 */
    RETURN_STATUE_FROM_DEVICE(3),

    /** 设备推送当前状态 */
    PUSH_STATUE_FROM_DEVICE(4);

    /** cmd 命令的值 */
    private int command;

    XPGCmdCommand(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }
}
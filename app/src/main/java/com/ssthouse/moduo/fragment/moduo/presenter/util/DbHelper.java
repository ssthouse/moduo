package com.ssthouse.moduo.fragment.moduo.presenter.util;

import com.activeandroid.query.Select;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库管理类:
 * Created by ssthouse on 2016/2/1.
 */
public class DbHelper {

    /**
     * 保存msgBean到数据库
     *
     * @param msgBean
     */
    public static void saveMsgBean(MsgBean msgBean) {
        msgBean.save();
    }

    /**
     * 找出之前10条消息:
     * 不可再UI线程执行
     * 获取的msgBean是时间倒序的
     *
     * @param lastMsgBean 最近一条msgBean用来获取时间戳
     * @return 最近10条msgBean或null
     */
    public static List<MsgBean> getLastTenMsgBean(MsgBean lastMsgBean) {
        if (lastMsgBean == null || lastMsgBean.getTimeStamp() == 0) {
            return new ArrayList<>();
        } else {
            return new Select()
                    .from(MsgBean.class)
                    .where(MsgBean.COLUMN_TIME_STAMP + " < " + lastMsgBean.getTimeStamp())
                    .orderBy(MsgBean.COLUMN_TIME_STAMP + " DESC")
                    .limit(10)
                    .execute();
        }
    }

    /**
     * 返回最近的10条msg
     *
     * @return
     */
    public static List<MsgBean> getInitTenMsg() {
        return new Select()
                .from(MsgBean.class)
                .orderBy(MsgBean.COLUMN_TIME_STAMP + " DESC")
                .limit(10)
                .execute();
    }
}

package com.ssthouse.moduo.fragment.moduo.view;

import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;

import java.util.List;

/**
 * UI控制操作提取
 * Created by ssthouse on 2016/2/14.
 */
public interface ModuoFragmentView {

    //变大
    void animate2Big();

    //变小
    void animate2Small();

    //加载更多msg
    void loadMoreMsg(List<MsgBean> msgList);

    //添加一条msg
    void addMsgBean(MsgBean msgBean);

    //获取最上一条msgBean
    MsgBean getTopMsgBean();
}

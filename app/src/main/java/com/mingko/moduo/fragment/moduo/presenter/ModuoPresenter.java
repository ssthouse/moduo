package com.mingko.moduo.fragment.moduo.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.mingko.moduo.control.util.Toast;
import com.mingko.moduo.control.xpg.CmdBean;
import com.mingko.moduo.control.xpg.DeviceBean;
import com.mingko.moduo.control.xpg.XPGController;
import com.mingko.moduo.control.xpg.TvControlBean;
import com.mingko.moduo.fragment.moduo.model.event.ModuoScaleChangeEvent;
import com.mingko.moduo.fragment.moduo.model.event.SpeechUnderstandEvent;
import com.mingko.moduo.fragment.moduo.presenter.util.DbHelper;
import com.mingko.moduo.fragment.moduo.model.ModuoModel;
import com.mingko.moduo.fragment.moduo.view.ModuoFragmentView;
import com.mingko.moduo.fragment.moduo.view.adapter.MsgBean;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * presenter
 * Created by ssthouse on 2016/2/14.
 */
public class ModuoPresenter {

    //View Model
    private ModuoFragmentView mModuoFragmentView;
    private ModuoModel mModuoModel;

    private Context mContext;

    //构造方法
    public ModuoPresenter(Context context, ModuoFragmentView moduoFragmentView) {
        this.mContext = context;
        this.mModuoFragmentView = moduoFragmentView;
        mModuoModel = new ModuoModel();
        EventBus.getDefault().register(this);
    }

    //加载更多msg
    public void loadMoreMsg() {
        mModuoModel.loadMoreMsg(mModuoFragmentView.getTopMsgBean())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MsgBean>>() {
                    @Override
                    public void call(List<MsgBean> msgBeans) {
                        //刷新UI
                        mModuoFragmentView.loadMoreMsg(msgBeans);
                    }
                });
    }

    //魔哆变大事件回调
    public void onEventMainThread(ModuoScaleChangeEvent event) {
        if (event.isToBig()) {
            mModuoFragmentView.animate2Big();
        } else {
            mModuoFragmentView.animate2Small();
        }
    }

    //添加msgBen回调
    public void onEventMainThread(MsgBean msgBean) {
        if (msgBean == null) {
            return;
        }
        //魔哆变小
        mModuoFragmentView.animate2Small();
        //添加数据到对话框
        mModuoFragmentView.addMsgBean(msgBean);
        //保存到数据库
        DbHelper.saveMsgBean(msgBean);
    }

    //语义理解回调
    public void onEventMainThread(SpeechUnderstandEvent event) {
        //必须先连接着魔哆
        if (XPGController.getCurrentDevice() == null) {
            Toast.show("当前未连接魔哆, 请连接后重试");
            return;
        }
        if (event.isSuccess()) {
            //解析出命令---发出msgBean
            Gson gson = new Gson();
            DeviceBean deviceBean = gson.fromJson(event.getJsonResult(), DeviceBean.class);
            //发送CmdBean
            CmdBean cmdBean = deviceBean.generateCmdBean();
            if (cmdBean == null) {
                EventBus.getDefault().post(MsgBean.getInstance(MsgBean.TYPE_MODUO_TEXT, MsgBean.STATE_SENT, "抱歉,您的指令:\t" + deviceBean.getText() + "\t未能执行"));
            } else {
                XPGController.getCurrentDevice().cWriteCmdCtrl(cmdBean);
                EventBus.getDefault().post(MsgBean.getInstance(MsgBean.TYPE_MODUO_TEXT, MsgBean.STATE_SENT, "已发送您的指令:" + deviceBean.getText()));
            }
        } else {
            EventBus.getDefault().post(MsgBean.getInstance(MsgBean.TYPE_MODUO_TEXT, MsgBean.STATE_SENT, "抱歉未能理解您的意思?"));
        }
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

}

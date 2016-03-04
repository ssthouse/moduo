package com.ssthouse.moduo.fragment.moduo.presenter;

import android.content.Context;

import com.ssthouse.moduo.control.xpg.CmdBean;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.fragment.moduo.bean.event.ModuoBigEvent;
import com.ssthouse.moduo.fragment.moduo.control.util.DbHelper;
import com.ssthouse.moduo.fragment.moduo.model.ModuoModel;
import com.ssthouse.moduo.fragment.moduo.view.ModuoFragmentView;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;
import com.ssthouse.moduo.model.event.xpg.XpgDeviceLoginEvent;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    //登陆
    public void login() {
        SettingManager settingManager = SettingManager.getInstance(mContext);
        XPGController.getCurrentDevice().getXpgWifiDevice().login(
                settingManager.getUid(),
                settingManager.getToken()
        );
    }

    //设备登陆回调
    public void onEventMainThread(XpgDeviceLoginEvent event){

    }

    //魔哆变大事件回调
    public void onEventMainThread(ModuoBigEvent event) {
        mModuoFragmentView.animate2Big();
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

        //todo---测试在这里发送数据
        byte data[] = {2, 2, 2, 2};
        XPGController.getInstance(mContext).getmCenter().cWriteCmdCtrl(
                XPGController.getCurrentDevice().getXpgWifiDevice(), new CmdBean(data[0], data[1], data[2], data[3])
        );
    }

    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

}

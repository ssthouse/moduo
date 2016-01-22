package com.ssthouse.moduo.main.view.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.video.CallingResponseEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.control.util.CloudUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.VideoActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 正在calling
 * 需要先登录设备
 * Created by ssthouse on 2016/1/12.
 */
public class CallingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.fragment_calling, container, false);
        initView(rootView);

        //登陆设备
        XPGController.getCurrentDevice().getXpgWifiDevice().login(
                SettingManager.getInstance(getContext()).getUid(),
                SettingManager.getInstance(getContext()).getToken()
        );
        return rootView;
    }


    private void initView(View rootView) {
        //todo---模拟接通电话
        rootView.findViewById(R.id.id_iv_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CallingResponseEvent(true));
            }
        });

        //取消
        rootView.findViewById(R.id.id_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出Activity
                getActivity().finish();
            }
        });
    }

    /**
     * 设备登陆回调---登陆才能进行操作
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("设备登陆成功");
            CloudUtil.updateUserInfoToLocal(getContext(), SettingManager.getInstance(getContext()).getUserName());
        } else {
            getActivity().finish();
            ToastHelper.show(getContext(), "设备登陆失败");
        }
    }

    /**
     * 电话接听回调
     *
     * @param event
     */
    public void onEventMainThread(CallingResponseEvent event) {
        Timber.e("收到电话接通结果回调");
        if (event.isSuccess()) {
            VideoActivity videoActivity = (VideoActivity) getActivity();
            videoActivity.showVideoFragment();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

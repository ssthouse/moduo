package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.video.CallingResponseEvent;
import com.ssthouse.moduo.main.view.activity.VideoActivity;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 正在calling
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

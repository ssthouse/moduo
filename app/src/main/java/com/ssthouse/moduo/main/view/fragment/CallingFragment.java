package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.video.CallingResponseEvent;

import de.greenrobot.event.EventBus;

/**
 * 正在calling
 * Created by ssthouse on 2016/1/12.
 */
public class CallingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calling, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        rootView.findViewById(R.id.id_iv_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CallingResponseEvent(true));
            }
        });

    }
}

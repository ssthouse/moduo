package com.ssthouse.moduo.main.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ssthouse.moduo.R;

/**
 * 分享设备fragment
 * Created by ssthouse on 2016/1/13.
 */
public class ShareDeviceFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_share_device, container, false);

        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        ImageView ivQrcode = (ImageView) rootView.findViewById(R.id.id_iv_qr_code);

        //// TODO: 2016/1/13 加载二维码(试试Rxjava) 
    }
}

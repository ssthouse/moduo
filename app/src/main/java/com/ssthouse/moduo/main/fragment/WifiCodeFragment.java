package com.ssthouse.moduo.main.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.util.NetUtil;
import com.ssthouse.moduo.main.control.util.QrCodeUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 展示wifi二维码
 * Created by ssthouse on 2016/1/20.
 */
public class WifiCodeFragment extends Fragment {

    private MaterialDialog wifiPasscodeDialog;
    private MaterialDialog waitDialog;

    private ImageView ivQrCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wifi_code, container, false);
        initView(rootView);
        showWifiDialog();
        return rootView;
    }

    private void initView(View rootView) {
        ivQrCode = (ImageView) rootView.findViewById(R.id.id_iv_qr_code);
        ivQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiPasscodeDialog.show();
            }
        });

        wifiPasscodeDialog = new MaterialDialog.Builder(getActivity())
                .autoDismiss(false)
                .customView(R.layout.dialog_wifi_passcode, true)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //先获取数据
                        View customView = materialDialog.getCustomView();
                        EditText etSsid = (EditText) customView.findViewById(R.id.id_et_wifi_ssid);
                        EditText etPassword = (EditText) customView.findViewById(R.id.id_et_wifi_password);
                        if (!TextUtils.isEmpty(etSsid.getText()) && !TextUtils.isEmpty(etPassword.getText())) {
                            //// TODO: 2016/1/13 生成二维码图片---显示在界面上
                            String url = QrCodeUtil.getWifiQrCodeContent(etSsid.getText().toString(),
                                    etPassword.getText().toString());
                            loadQrCode(url);
                            //隐藏dialog
                            wifiPasscodeDialog.dismiss();
                        } else {
                            ToastHelper.show(getActivity(), "wifi名称和密码不可为空");
                        }
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //隐藏dialog
                        wifiPasscodeDialog.dismiss();
                    }
                })
                .build();

        waitDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_wait, true)
                .cancelable(false)
                .build();
    }


    /**
     * 加载URL二维码到imageView
     *
     * @param url
     */
    private void loadQrCode(String url) {
        showWaitDialog("正在生成二维码");
        Observable.just(url)
                //将url解析为二维码
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return QrCodeUtil.generateQRCode(s);
                    }
                })
                //子线程生成二维码
                .subscribeOn(Schedulers.newThread())
                //UI线程加载图片
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        ivQrCode.setImageBitmap(bitmap);
                        waitDialog.dismiss();
                    }
                });
    }

    /**
     * 弹出输入wifi的dialog
     */
    private void showWifiDialog() {
        String ssid = NetUtil.getCurentWifiSSID(getActivity());
        EditText etSsid = (EditText) wifiPasscodeDialog.getCustomView().findViewById(R.id.id_et_wifi_ssid);
        etSsid.setText(ssid);
        wifiPasscodeDialog.show();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }
}

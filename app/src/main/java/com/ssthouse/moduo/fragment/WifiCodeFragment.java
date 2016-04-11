package com.ssthouse.moduo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.QrCodeUtil;
import com.ssthouse.moduo.control.util.Toast;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 将wifi名称和密码...用二维码展现出来  供魔哆扫描
 * Created by ssthouse on 2016/1/20.
 */
public class WifiCodeFragment extends Fragment {

    //wifi密码输入dialog
    private View wifiPasscodeDialogView;
    private Dialog wifiPasscodeDialog;
    //等待dialog
    private View waitDialogView;
    private Dialog waitDialog;

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

        wifiPasscodeDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wifi_passcode, null);
        wifiPasscodeDialog = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.dialog_wifi_passcode, false)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //先获取数据
                        View customView = materialDialog.getCustomView();
                        EditText etSsid = (EditText) customView.findViewById(R.id.id_et_wifi_ssid);
                        EditText etPassword = (EditText) customView.findViewById(R.id.id_et_wifi_password);
                        if (!TextUtils.isEmpty(etSsid.getText()) && !TextUtils.isEmpty(etPassword.getText())) {
                            //生成二维码图片---显示在界面上
                            String url = QrCodeUtil.getWifiQrCodeContent(etSsid.getText().toString(),
                                    etPassword.getText().toString());
                            loadQrCode(url);
                            //隐藏dialog
                            wifiPasscodeDialog.dismiss();
                        } else {
                            Toast.show("wifi名称和密码不可为空");
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

        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getActivity())
                .setView(waitDialogView)
                .create();
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
        //如果连接着wifi---显示默认连接的wifi名称
        if(NetUtil.isWifi(getContext())) {
            String ssid = NetUtil.getCurentWifiSSID(getActivity());
            EditText etSsid = (EditText) wifiPasscodeDialogView.findViewById(R.id.id_et_wifi_ssid);
            etSsid.setText(ssid);
        }
        wifiPasscodeDialog.show();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }
}

package com.ssthouse.moduo.fragment.sliding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.QrCodeUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.device.Device;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 分享设备fragment
 * Created by ssthouse on 2016/1/13.
 */
public class ShareModuoFragment extends Fragment implements IFragmentUI {

    private ImageView ivQrcode;
    private MaterialDialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_device, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        ivQrcode = (ImageView) rootView.findViewById(R.id.id_iv_qr_code);

        waitDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .autoDismiss(false)
                .build();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    /**
     * 刷新fragment UI
     */
    @Override
    public void updateUI() {
        final Device currentDevice = XPGController.getCurrentDevice();
        if (currentDevice == null) {
            Toast.show("当前未连接魔哆");
        } else {
            showWaitDialog("正在生成二维码请稍候");
            String urlContent = QrCodeUtil.getDeviceQrCodeContent(
                    currentDevice.getXpgWifiDevice().getProductKey(),
                    currentDevice.getXpgWifiDevice().getDid(),
                    currentDevice.getXpgWifiDevice().getPasscode(),
                    currentDevice.getVideoCidNumber() + "",
                    currentDevice.getVideoUsername(),
                    currentDevice.getVideoPassword());
            Observable.just(urlContent)
                    .map(new Func1<String, Bitmap>() {
                        @Override
                        public Bitmap call(String s) {
                            return QrCodeUtil.generateQRCode(s);
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            ivQrcode.setImageBitmap(bitmap);
                            waitDialog.dismiss();
                            //保存did二维码图片
                            Observable.just(bitmap)
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(new Action1<Bitmap>() {
                                        @Override
                                        public void call(Bitmap bitmap) {
                                            FileUtil.saveBitmap(bitmap,
                                                    currentDevice.getXpgWifiDevice().getDid());
                                        }
                                    });
                        }
                    });
        }
    }
}

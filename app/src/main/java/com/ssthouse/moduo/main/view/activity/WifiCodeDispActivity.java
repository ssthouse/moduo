package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.scan.ModuoMessageEvent;
import com.ssthouse.moduo.main.control.util.NetUtil;
import com.ssthouse.moduo.main.control.util.QrCodeUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 手机连接的wifi形成二维码---供魔哆扫描
 * Created by ssthouse on 2016/1/13.
 */
public class WifiCodeDispActivity extends AppCompatActivity {

    private MaterialDialog wifiPasscodeDialog;

    private MaterialDialog waitDialog;

    private ImageView ivQrCode;

    public static void start(Context context) {
        Intent intent = new Intent(context, WifiCodeDispActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_code_disp);
        EventBus.getDefault().register(this);

        initView();

        showWifiDialog();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("连接绑定");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivQrCode = (ImageView) findViewById(R.id.id_iv_qr_code);

        wifiPasscodeDialog = new MaterialDialog.Builder(this)
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
                            ToastHelper.show(WifiCodeDispActivity.this, "wifi名称和密码不可为空");
                        }
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //取消直接finish
                        finish();
                    }
                })
                .build();

        waitDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_wait, true)
                .cancelable(false)
                .build();
    }

    /**
     * 弹出输入wifi的dialog
     */
    private void showWifiDialog() {
        String ssid = NetUtil.getCurentWifiSSID(this);
        EditText etSsid = (EditText) wifiPasscodeDialog.getCustomView().findViewById(R.id.id_et_wifi_ssid);
        etSsid.setText(ssid);
        wifiPasscodeDialog.show();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
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
     * 魔哆连上wifi---发出消息事件
     *
     * @param event
     */
    public void onEventMainThread(ModuoMessageEvent event) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

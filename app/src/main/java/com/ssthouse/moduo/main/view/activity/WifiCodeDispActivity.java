package com.ssthouse.moduo.main.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.scan.ModuoMessageEvent;

import de.greenrobot.event.EventBus;

/**
 * 手机连接的wifi形成二维码---供魔哆扫描
 * Created by ssthouse on 2016/1/13.
 */
public class WifiCodeDispActivity extends AppCompatActivity {

    private MaterialDialog wifiPasscodeDialog;

    private ImageView ivQrCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_code_disp);
        EventBus.getDefault().register(this);

        initView();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("连接绑定");

        ivQrCode = (ImageView) findViewById(R.id.id_iv_qr_code);

        wifiPasscodeDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wifi_passcode, true)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //// TODO: 2016/1/13 生成二维码图片---显示在界面上

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
    }

    /**
     * 魔哆连上wifi---发出消息事件
     *
     * @param event
     */
    public void onEventMainThread(ModuoMessageEvent event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

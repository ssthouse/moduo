package com.ssthouse.moduo.view.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.SessionStateEvent;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();

        //初始化视频sdk
        communication = Communication.getInstance(this);
    }

    public void onEventMainThread(SessionStateEvent event){

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add_device:
                //TODO---弹出添加设备dialog
                showAddDeviceDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示添加设备的dialog
     */
    private void showAddDeviceDialog() {
        new MaterialDialog.Builder(this)
                .autoDismiss(true)
                .title("添加设备")
                .customView(R.layout.dialog_add_device, true)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //TODO---确定添加设备回调
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //TODO--取消回调
                    }
                })
                .build()
                .show();
    }
}

package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.Device;
import com.ssthouse.moduo.model.event.ActionProgressEvent;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;
import com.ssthouse.moduo.model.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.view.adapter.MainLvAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_IS_LOGIN_SUCCESS = "isLoginSuccess";

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    /**
     * 当前的所有设备
     */
    private List<Device> deviceList;

    @Bind(R.id.id_tv_offline)
    TextView tvOffline;

    /**
     * 主界面listview
     */
    @Bind(R.id.id_lv_main)
    ListView lv;

    private MainLvAdapter lvAdapter;

    /**
     * actionbar上进度条item
     */
    private MenuItem pbItem;

    /**
     * 启动当前activity
     * @param context
     * @param isLoginSuccess
     */
    public static void start(Context context, boolean isLoginSuccess) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_IS_LOGIN_SUCCESS, isLoginSuccess);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();

        //初始化视频sdk
        communication = Communication.getInstance(this);

        //加载本地添加过的设备
        initLocalDevice();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //显示是否登陆平台成功
        boolean isLoginSuccess = getIntent().getBooleanExtra(EXTRA_IS_LOGIN_SUCCESS, false);
        if(!isLoginSuccess) {
            tvOffline.setVisibility(View.VISIBLE);
        }

        deviceList = PreferenceHelper.getInstance(this).getDeviceList();
        lvAdapter = new MainLvAdapter(this, deviceList);
        lv.setAdapter(lvAdapter);
    }

    /**
     * 加载本地添加过的设备
     */
    private void initLocalDevice() {
        //TODO---启动最近的设别
        for (Device device : deviceList) {
            communication.addStreamer(device.getCidNumber(), device.getUsername(), device.getPassword());
        }
    }

    /**
     * 和设备连接状态事件
     *
     * @param event
     */
    public void onEventMainThread(SessionStateEvent event) {
        //TODO---设备状态变化--显示离线view
        switch (event.getSessionState()) {
            case CONNECTED:
                tvOffline.setVisibility(View.GONE);
                break;
            case DISCONNECTED:
                tvOffline.setVisibility(View.VISIBLE);
                break;
            case INVALID:
                tvOffline.setText("当前链接已失效, 请重新登录...");
                tvOffline.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 采集端连接状态回调
     *
     * @param event
     */
    public void onEventMainThread(StreamerConnectChangedEvent event) {
        Timber.e("我接收到设备状态更新");
        //修改设备状态
        for (Device device : deviceList) {
            if (device.getCidNumber() == event.getCidNumber()) {
                device.setStreamerPresenceState(event.getState());
                Timber.e("我更新了设备状态");
            }
        }
        //更新界面
        lvAdapter.update();
    }

    /**
     * 接收actionbar上progressbar事件
     *
     * @param event
     */
    public void onEventMainThread(ActionProgressEvent event) {
        if (event.isShow()) {
            pbItem.setVisible(true);
        } else {
            pbItem.setVisible(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        pbItem = menu.findItem(R.id.id_action_pb);
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
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .autoDismiss(true)
                .title("添加设备")
                .customView(R.layout.dialog_add_device, true)
                .positiveText("确认")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //TODO---确定添加设备回调
                        View customView = materialDialog.getCustomView();
                        EditText etCidNumber = (EditText) customView.findViewById(R.id.id_et_cid_number);
                        EditText etUsername = (EditText) customView.findViewById(R.id.id_et_username);
                        EditText etPassword = (EditText) customView.findViewById(R.id.id_et_password);
                        //TODO---建立连接
                        communication.addStreamer(Long.valueOf(etCidNumber.getText().toString()),
                                etUsername.getText().toString(), etPassword.getText().toString());
                        Device device = new Device(Long.valueOf(etCidNumber.getText().toString()),
                                etUsername.getText().toString(),
                                etPassword.getText().toString());
                        PreferenceHelper.getInstance(MainActivity.this).addDevice(device);
                        //TODO---刷新列表
                        deviceList.add(device);
                        lvAdapter.update();
                    }
                })
                .negativeText("取消")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        //TODO--取消回调
                    }
                })
                .build();
        materialDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        communication.destory();//销毁sdk
        android.os.Process.killProcess(android.os.Process.myPid());//确保完全退出，释放所有资源
    }
}

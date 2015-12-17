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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.ActionProgressEvent;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;
import com.ssthouse.moduo.view.adapter.MainLvAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

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

    public static void start(Context context){
        Intent intent = new Intent(context, MainActivity.class);
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
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lvAdapter = new MainLvAdapter(this);
        lv.setAdapter(lvAdapter);
    }

    /**
     * 和设备连接状态事件
     * @param event
     */
    public void onEventMainThread(SessionStateEvent event){

    }

    /**
     * 接收actionbar上progressbar事件
     * @param event
     */
    public void onEventMainThread(ActionProgressEvent event){
        if(event.isShow()){
            pbItem.setVisible(true);
        }else{
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
                                etUsername.getText().toString(),etPassword.getText().toString());
                        PreferenceHelper.getInstance(MainActivity.this).addDevice(etCidNumber.getText().toString());
                        //TODO---刷新列表
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
    protected void onDestroy()
    {
        super.onDestroy();
        communication.destory();//销毁sdk
        android.os.Process.killProcess(android.os.Process.myPid());//确保完全退出，释放所有资源
    }
}

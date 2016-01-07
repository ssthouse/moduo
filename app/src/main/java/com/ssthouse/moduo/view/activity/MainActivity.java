package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.ScanUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.Constant;
import com.ssthouse.moduo.model.Device;
import com.ssthouse.moduo.model.event.ActionProgressEvent;
import com.ssthouse.moduo.model.event.MainActivityRefreshEvent;
import com.ssthouse.moduo.model.event.NetworkStateChangeEvent;
import com.ssthouse.moduo.model.event.video.SessionStateEvent;
import com.ssthouse.moduo.model.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.model.event.xpg.DeviceStateEvent;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.model.event.xpg.GetDeviceDataEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.scan.ScanCons;
import com.ssthouse.moduo.view.adapter.MainLvAdapter;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_IS_LOGIN_SUCCESS = "isLoginSuccess";
    private long exitTimeInMils = 0;
    /**
     * 是否是退出到loading activity
     * 决定是否要kill 当前线程
     */
    private boolean isLogOut = false;

    /**
     * 是否在离线状态
     */
    private boolean isOffline;

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    /**
     * 当前的所有设备
     */
    private List<Device> deviceList = new ArrayList<>();

    /**
     * 下拉刷新view
     */
    @Bind(R.id.id_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 无网络连接提示
     */
    @Bind(R.id.id_tv_offline)
    TextView tvOffline;
    /**
     * 主界面listview
     */
    @Bind(R.id.id_lv_main)
    ListView lv;

    private MaterialDialog waitDialog;

    private MainLvAdapter lvAdapter;

    /**
     * actionbar上进度条item
     */
    private MenuItem pbItem;

    /**
     * 启动当前activity
     *
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

        isOffline = NetUtil.isConnected(this);

        //加载本地添加过的设备
        initDeviceList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tb);
        setSupportActionBar(toolbar);

        //TODO--换个方式告知用户---显示是否登陆视频对话平台成功
        boolean isLoginSuccess = getIntent().getBooleanExtra(EXTRA_IS_LOGIN_SUCCESS, false);
        if (!isLoginSuccess) {
            tvOffline.setVisibility(View.VISIBLE);
        }

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //// TODO: 2015/12/25 刷新设备状态
                reConnectDevice();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //请求获取绑定了的设备--初始化设备listview
        lvAdapter = new MainLvAdapter(this, deviceList);
        lv.setAdapter(lvAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO---尝试登陆
                if (!deviceList.get(position).getXpgWifiDevice().isOnline()) {
                    deviceList.get(position).getXpgWifiDevice().login(
                            SettingManager.getInstance(MainActivity.this).getUid(),
                            SettingManager.getInstance(MainActivity.this).getToken()
                    );
                }
                Timber.e("clicked");
            }
        });

        //添加设备
        findViewById(R.id.id_btn_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanUtil.startScan(MainActivity.this);
            }
        });

        //初始化dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 加载账号绑定的设备
     */
    private void initDeviceList() {
        //显示等待Dialog
        showWaitDialog("正在获取设备");
        //请求获取设备列表
        XPGController.getInstance(this).getmCenter()
                .cGetBoundDevices(SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken());
    }

    /**
     * 将当前设备重新连接sdk
     */
    private void reConnectDevice() {
        //尝试登陆机智云
        if (!Constant.isXpgLogin) {
            //匿名登录
            XPGController.getInstance(this).getmCenter().cLoginAnonymousUser();
        }
        //尝试连接每台设备
        for (Device device : deviceList) {
            //登陆视频sdk
            communication.addStreamer(device.getCidNumber(), device.getUsername(), device.getPassword());
            //登陆机智云sdk
            device.getXpgWifiDevice().setListener(XPGController.getInstance(this).getDeviceListener());
            device.getXpgWifiDevice().login(SettingManager.getInstance(this).getUid(),
                    SettingManager.getInstance(this).getToken());
        }
        lvAdapter.update();
    }

    /*
    UI回调
     */

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

    /**
     * 主界重新加载设备回调
     *
     * @param event
     */
    public void onEventMainThread(MainActivityRefreshEvent event) {
        //重新获取绑定设备
        XPGController.getInstance(this).getmCenter().cGetBoundDevices(
                SettingManager.getInstance(this).getUid(),
                SettingManager.getInstance(this).getToken()
        );
    }

    /**
     * 网络状态变化的回调
     *
     * @param event
     */
    public void onEventMainThread(NetworkStateChangeEvent event) {
        switch (event.getNetworkState()) {
            case NONE:
                isOffline = true;
                ToastHelper.show(this, "网络连接已断开");
                tvOffline.setVisibility(View.VISIBLE);
                setViewDisable();
                break;
            case MOBILE:
                //如果之前是离线状态---需要重新连接(视频会不断自动连接---机智云不会--需要手动重新连接)
                if (isOffline) {
                    reConnectDevice();
                }
                tvOffline.setVisibility(View.GONE);
                break;
            case WIFI:
                tvOffline.setVisibility(View.GONE);
                if (isOffline) {
                    reConnectDevice();
                }
                break;
        }
        lvAdapter.update();
    }

    /*
    视频SDK回调
     */

    /**
     * 视频设备连接状态事件
     *
     * @param event
     */
    public void onEventMainThread(SessionStateEvent event) {
        //刷新lv
        lvAdapter.update();
    }

    /**
     * 视频采集端连接状态回调
     *
     * @param event
     */
    public void onEventMainThread(StreamerConnectChangedEvent event) {
        Timber.e("我接收到视频sdk状态更新");
        //修改设备状态
        for (Device device : deviceList) {
            if (device.getCidNumber() == event.getCidNumber()) {
                device.setStreamerPresenceState(event.getState());
                Timber.e("我更新了视频sdk状态");
            }
        }
        //更新界面
        lvAdapter.update();
    }

    /*
    机智云回调
     */

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(this, "MainActivity")) {
            Timber.e("MainActivity不在最前");
            return;
        }
        if (event.isSuccess()) {
            //改变全局状态
            Constant.isXpgLogin = true;
            Timber.e("机智云---登录成功");
            ToastHelper.show(this, "登陆成功!");
            //保存机智云登陆数据
            SettingManager.getInstance(this).setLoginInfo(event);
            //获取设备
            initDeviceList();
        } else {
            ToastHelper.show(this, "登陆失败");
        }
    }

    /**
     * 机智云设备绑定回调
     *
     * @param event
     */
    public void onEventMainThread(DeviceBindResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("设备绑定成功");
            ToastHelper.show(this, "设备绑定成功");
            //TODO---应该发出获取账号绑定的设备列表的请求--都在这个请求的回调里面进行UI更新比较好
            XPGController.getInstance(this).getmCenter().cGetBoundDevices(
                    SettingManager.getInstance(this).getUid(),
                    SettingManager.getInstance(this).getToken());
        } else {
            Timber.e("设备绑定失败");
            ToastHelper.show(this, "设备绑定失败");
        }
        //隐藏dialog
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    /**
     * 获取账号绑定设备
     *
     * @param event
     */
    public void onEventMainThread(GetBoundDeviceEvent event) {
        if (event.isSuccess()) {
            //清空当前列表
            deviceList.clear();
            //刷新主界面lv列表
            Timber.e("获取账号绑定设备列表成功");
            ToastHelper.show(this, "获取绑定设备成功,设备数目:\t" + event.getXpgDeviceList().size());
            for (XPGWifiDevice xpgWifiDevice : event.getXpgDeviceList()) {
                //设置监听器
                xpgWifiDevice.setListener(XPGController.getInstance(this).getDeviceListener());
                //设备登陆
                xpgWifiDevice.login(SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken());
                //添加到deviceList
                deviceList.add(new Device(this, xpgWifiDevice));
            }
            //尝试连接 视频对话
            for (Device device : deviceList) {
                communication.addStreamer(device.getCidNumber(), device.getUsername(), device.getPassword());
            }
            //更新lv
            lvAdapter.update();
        } else {
            Timber.e("获取账号绑定设备列表失败");
            ToastHelper.show(this, "获取设备列表失败");
        }
        //隐藏等待dialog
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    /**
     * 设备状态发生变化事件
     *
     * @param event
     */
    public void onEventMainThread(DeviceStateEvent event) {
        lvAdapter.update();
    }

    /**
     * 获取设备数据事件
     *
     * @param event
     */
    public void onEventMainThread(GetDeviceDataEvent event) {
        //只有当activity在最前时进行相应
        if (!ActivityUtil.isTopActivity(this, "MainActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //TODO---进行跳转
            //启动配置activity
            Timber.e("我启动了设备配置activity");
            XpgControlActivity.start(this, event.getInitDeviceData());
        } else {
            Timber.e("设备数据获取失败");
            ToastHelper.show(this, "设备数据获取失败");
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
            case R.id.id_action_get_bound_device:
                //TODO---获取账号绑定设备
                //显示等待Dialog
                showWaitDialog("正在加载设备");
                //请求获取设备列表
                XPGController.getInstance(this).getmCenter().cGetBoundDevices(
                        SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken());
                break;
            case R.id.id_action_log_out:
                //TODO---登出
                SettingManager.getInstance(this).clean();
                PreferenceHelper.getInstance(this).setIsFistIn(true);
                //重新进入loading activity
                LoadingActivity.start(this);
                //结束当前activity
                isLogOut = true;
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示等待Dialog
     *
     * @param msg Dialog显示的msg
     */
    public void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    /**
     * 将视频和设备控制界面设为不可用
     */
    private void setViewDisable() {
        int childCount = lv.getChildCount();
        Timber.e("一共有" + childCount + "个child");
        for (int i = 0; i < childCount; i++) {
            View childView = lv.getChildAt(i);
            //设备不在线
            TextView tvXpgState = (TextView) childView.findViewById(R.id.id_tv_xpg_state);
            tvXpgState.setText("离线");
            //视频不在线
            TextView tvVideoState = (TextView) childView.findViewById(R.id.id_tv_video_state);
            tvVideoState.setText("离线");
            //设备控制按钮
            Button btnXpgControl = (Button) childView.findViewById(R.id.id_btn_xpg_control);
            btnXpgControl.setEnabled(false);
            //视频通话按钮
            Button btnVideo = (Button) childView.findViewById(R.id.id_btn_start_video);
            btnVideo.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        communication.destory();//销毁sdk
        //todo---完全退出程序(若不是跳转到登陆界面)
        if (!isLogOut) {
            android.os.Process.killProcess(android.os.Process.myPid());//确保完全退出，释放所有资源
        }
    }

    @Override
    public void onBackPressed() {
        //如果上一次点击事件少一点五秒
        if (System.currentTimeMillis() < (exitTimeInMils + 1500)) {
            super.onBackPressed();
        } else {
            exitTimeInMils = System.currentTimeMillis();
            ToastHelper.show(this, "再次点击退出");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 获取扫描二维码的结果
         */
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Timber.e("Cancelled scan");
            } else {
                String text = result.getContents();
                //机智云sdk参数
                String product_key = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PRODUCT_KEY);
                String did = ScanUtil.getParamFromUrl(text, ScanCons.KEY_DID);
                String passCode = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PASSCODE);
                //视频sdk参数
                String cidStr = ScanUtil.getParamFromUrl(text, ScanCons.KEY_CID_NUMBER);
                String username = ScanUtil.getParamFromUrl(text, ScanCons.KEY_USER_NAME);
                String password = ScanUtil.getParamFromUrl(text, ScanCons.KEY_PASSWORD);
                //判断二维码扫描数据是否正确
                if (product_key == null
                        || did == null
                        || passCode == null
                        || cidStr == null
                        || username == null
                        || password == null) {
                    ToastHelper.showLong(this, "请扫描正确的二维码");
                    return;
                }
                long cidNumber = Long.parseLong(cidStr);
                Timber.e("机智云参数: " + "product_key:\t" + product_key + "\tdid:\t" + did + "\tpasscode:\t" + passCode);
                Timber.e("视频sdk参数: " + "cidNumber:\t" + cidNumber + "\tusername:\t" + username + "\tpassword:\t" + password);
                //将当前设备数据保存在本地
                PreferenceHelper.getInstance(this).addDevice(did, cidNumber, username, password);
                //显示等待dialog
                showWaitDialog("正在绑定设备");
                //尝试绑定
                XPGController.getInstance(this).getmCenter().cBindDevice(
                        SettingManager.getInstance(this).getUid(),
                        SettingManager.getInstance(this).getToken(),
                        did,
                        passCode,
                        ""
                );
            }
        } else {
            Timber.e("二维码解析为空");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

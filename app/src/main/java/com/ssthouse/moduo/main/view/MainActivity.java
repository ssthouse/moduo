package com.ssthouse.moduo.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.ScanUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.bean.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.main.presenter.MainPresenter;
import com.ssthouse.moduo.main.presenter.MainPresenterImpl;
import com.ssthouse.moduo.cons.Constant;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.event.view.NetworkStateChangeEvent;
import com.ssthouse.moduo.bean.event.video.SessionStateEvent;
import com.ssthouse.moduo.bean.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.bean.event.xpg.GetBoundDeviceEvent;
import com.ssthouse.moduo.cons.scan.ScanCons;
import com.ssthouse.moduo.main.view.activity.LoadingActivity;
import com.ssthouse.moduo.main.view.activity.SettingActivity;
import com.ssthouse.moduo.main.view.adapter.MainLvAdapter;
import com.ssthouse.moduo.main.view.fragment.AboutModuoFragment;
import com.ssthouse.moduo.main.view.fragment.ShareDeviceFragment;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 当前activity不监听设备数据传达的event
 */
public class MainActivity extends AppCompatActivity implements MainView {

    private static final String EXTRA_IS_LOGIN_SUCCESS = "isLoginSuccess";
    /**
     * 点两次退出程序
     */
    private long exitTimeInMils = 0;
    /**
     * 是否是退出到loading activity
     * 决定是否要kill 当前线程
     */
    private boolean isLogOut = false;

    private FragmentManager fragmentManager;
    private ShareDeviceFragment shareDeviceFragment;
    private AboutModuoFragment aboutModuoFragment;

    /**
     * 是否在离线状态
     */
    private boolean isOffline;

    //Presenter
    private MainPresenter mainPresenter;

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    @Bind(R.id.id_drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.id_navigation_view)
    NavigationView navigationView;

    /**
     * 下拉刷新view
     */
    @Bind(R.id.id_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    /**
     * 主界面listview
     */
    @Bind(R.id.id_lv_main)
    ListView lv;

    private MaterialDialog waitDialog;

    private MainLvAdapter lvAdapter;

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

        //todo---初始化Presenter
        mainPresenter = new MainPresenterImpl(this, this);

        //初始化视频sdk
        communication = Communication.getInstance(this);

        isOffline = NetUtil.isConnected(this);

        //加载本地添加过的设备
        mainPresenter.initDeviceList();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tb);
        setSupportActionBar(toolbar);

        //初始化抽屉事件
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //// TODO: 2016/1/13  
                switch (item.getItemId()){
                    case R.id.id_menu_user_info:
//                        fragmentManager.beginTransaction()
                        break;
                    case R.id.id_menu_about_moduo:
                        break;
                    case R.id.id_menu_share_device:
                        break;
                    case R.id.id_menu_setting:
                        SettingActivity.start(MainActivity.this);
                        break;
                }
                //抽屉中的点击事件
                return true;
            }
        });

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Constant.isXpgLogin) {
                    mainPresenter.refreshDeviceList();
                } else {
                    //匿名登录
                    XPGController.getInstance(MainActivity.this).getmCenter().cLoginAnonymousUser();
                }
            }
        });

        //请求获取绑定了的设备--初始化设备listview
        lvAdapter = new MainLvAdapter(this, XPGController.getDeviceList());
        lv.setAdapter(lvAdapter);

        //初始化dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 刷新设备列表
     *
     * @param event
     */
    @Override
    public void updateDeviceList(GetBoundDeviceEvent event) {
        //先清空列表
        XPGController.getDeviceList().clear();
        //添加设备
        for (XPGWifiDevice xpgWifiDevice : event.getXpgDeviceList()) {
            //设置监听器
            xpgWifiDevice.setListener(XPGController.getInstance(this).getDeviceListener());
            //添加到deviceList
            XPGController.getDeviceList().add(new Device(this, xpgWifiDevice));
        }
        //尝试连接 视频对话
        for (Device device : XPGController.getDeviceList()) {
            communication.addStreamer(device.getCidNumber(), device.getUsername(), device.getPassword());
        }
        lvAdapter.update();
    }

    @Override
    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissDialog() {
        waitDialog.dismiss();
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        swipeRefreshLayout.setRefreshing(enable);
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
                setViewDisable();
                break;
            case MOBILE:
                //如果之前是离线状态---需要重新连接(视频会不断自动连接---机智云不会--需要手动重新连接)
                if (isOffline) {
                    mainPresenter.initDeviceList();
                }
                break;
            case WIFI:
                if (isOffline) {
                    mainPresenter.initDeviceList();
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
        for (Device device : XPGController.getDeviceList()) {
            if (device.getCidNumber() == event.getCidNumber()) {
                device.setStreamerPresenceState(event.getState());
                Timber.e("我更新了视频sdk状态");
            }
        }
        //更新界面
        lvAdapter.update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_get_bound_device:
                mainPresenter.initDeviceList();
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
        //// TODO: 2016/1/10
        mainPresenter.unRegister();
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
                //抛出扫描
                EventBus.getDefault().post(new ScanDeviceEvent(true, did, passCode));
            }
        } else {
            ToastHelper.show(this, "二维码解析为空");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

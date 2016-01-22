package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.device.Device;
import com.ssthouse.moduo.bean.event.scan.ScanDeviceEvent;
import com.ssthouse.moduo.bean.event.video.SessionStateEvent;
import com.ssthouse.moduo.bean.event.video.StreamerConnectChangedEvent;
import com.ssthouse.moduo.bean.event.video.ViewerLoginResultEvent;
import com.ssthouse.moduo.bean.event.xpg.DeviceBindResultEvent;
import com.ssthouse.moduo.main.control.util.FileUtil;
import com.ssthouse.moduo.main.control.util.QrCodeUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.video.Communication;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.view.activity.account.UserInfoEditActivity;
import com.ssthouse.moduo.main.view.fragment.main.AboutModuoFragment;
import com.ssthouse.moduo.main.view.fragment.main.IFragmentUI;
import com.ssthouse.moduo.main.view.fragment.main.MainFragment;
import com.ssthouse.moduo.main.view.fragment.main.ShareModuoFragment;
import com.ssthouse.moduo.main.view.fragment.main.UserInfoFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 当前activity不监听设备数据传达的event
 */
public class MainActivity extends AppCompatActivity {
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
    private ShareModuoFragment shareDeviceFragment;
    private AboutModuoFragment aboutModuoFragment;
    private UserInfoFragment userInfoFragment;
    private MainFragment mainFragment;

    /**
     * fragment切换逻辑
     */
    public enum FragmentState {
        SHARE_MODUO_FRAGMENT, ABOUT_MODUO_FRAGMENT, USER_INFO_FRAGMENT, MAIN_FRAGMENT;
    }

    public FragmentState currentFragmentState = FragmentState.MAIN_FRAGMENT;

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    @Bind(R.id.id_drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.id_navigation_view)
    NavigationView navigationView;

    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context
     */
    public static void start(Context context) {
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
        initFragment();

        //初始化视频sdk
        Communication.loadSdkLib();
        communication = Communication.getInstance(this);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        mainFragment = new MainFragment();
        userInfoFragment = new UserInfoFragment();
        aboutModuoFragment = new AboutModuoFragment();
        shareDeviceFragment = new ShareModuoFragment();
        //初始化为MainFragment
        fragmentManager.beginTransaction()
                .add(R.id.id_fragment_container, mainFragment)
                .add(R.id.id_fragment_container, userInfoFragment)
                .add(R.id.id_fragment_container, aboutModuoFragment)
                .add(R.id.id_fragment_container, shareDeviceFragment)
                .hide(userInfoFragment)
                .hide(aboutModuoFragment)
                .hide(shareDeviceFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_tb);
        setSupportActionBar(toolbar);

        //初始化抽屉事件
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        //抽屉中的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_menu_main:
                        switchFragment(FragmentState.MAIN_FRAGMENT);
                        break;
                    case R.id.id_menu_user_info:
                        switchFragment(FragmentState.USER_INFO_FRAGMENT);
                        break;
                    case R.id.id_menu_about_moduo:
                        switchFragment(FragmentState.ABOUT_MODUO_FRAGMENT);
                        break;
                    case R.id.id_menu_share_moduo:
                        switchFragment(FragmentState.SHARE_MODUO_FRAGMENT);
                        break;
                    case R.id.id_menu_setting:
                        SettingActivity.start(MainActivity.this);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        //初始化dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 切换fragment
     *
     * @param newState
     */
    private void switchFragment(FragmentState newState) {
        //隐藏当前fragment
        Fragment currentFragment = null;
        switch (currentFragmentState) {
            case MAIN_FRAGMENT:
                currentFragment = mainFragment;
                break;
            case USER_INFO_FRAGMENT:
                currentFragment = userInfoFragment;
                break;
            case ABOUT_MODUO_FRAGMENT:
                currentFragment = aboutModuoFragment;
                break;
            case SHARE_MODUO_FRAGMENT:
                currentFragment = shareDeviceFragment;
                break;
        }
        fragmentManager.beginTransaction().hide(currentFragment)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        //显示toFragment
        Fragment toFragment = null;
        switch (newState) {
            case MAIN_FRAGMENT:
                currentFragmentState = FragmentState.MAIN_FRAGMENT;
                toFragment = mainFragment;
                getSupportActionBar().setTitle("魔哆");
                break;
            case USER_INFO_FRAGMENT:
                currentFragmentState = FragmentState.USER_INFO_FRAGMENT;
                toFragment = userInfoFragment;
                getSupportActionBar().setTitle("个人资料");
                break;
            case ABOUT_MODUO_FRAGMENT:
                currentFragmentState = FragmentState.ABOUT_MODUO_FRAGMENT;
                toFragment = aboutModuoFragment;
                getSupportActionBar().setTitle("关于魔哆");
                break;
            case SHARE_MODUO_FRAGMENT:
                currentFragmentState = FragmentState.SHARE_MODUO_FRAGMENT;
                toFragment = shareDeviceFragment;
                getSupportActionBar().setTitle("生成二维码");
                break;
        }
        fragmentManager.beginTransaction().show(toFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        //刷新fragment的UI
        IFragmentUI fragment = (IFragmentUI) toFragment;
        fragment.updateUI();
        //更新menu
        invalidateOptionsMenu();
    }

    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    public void dismissDialog() {
        waitDialog.dismiss();
    }

    /*
    视频SDK回调
     */

    /**
     * 视频直播---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(ViewerLoginResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("视频直播---登录成功");
        } else {
            ToastHelper.show(this, "登陆视频sdk失败");
        }
    }

    /**
     * 视频设备连接状态事件
     *
     * @param event
     */
    public void onEventMainThread(SessionStateEvent event) {
        //刷新lv
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
            if (device.getVideoCidNumber() == event.getCidNumber()) {
                device.setStreamerPresenceState(event.getState());
                Timber.e("我更新了视频sdk状态");
            }
        }
        //更新界面
    }

    /**
     * 扫描设备回调
     *
     * @param event
     */
    public void onEventMainThread(ScanDeviceEvent event) {
        Timber.e("扫描Activity回调");
        if (event.isSuccess()) {
            showDialog("正在绑定设备,请稍候");
            //开始绑定设备
            XPGController.getInstance(this).getmCenter().cBindDevice(
                    SettingManager.getInstance(this).getUid(),
                    SettingManager.getInstance(this).getToken(),
                    event.getDid(),
                    event.getPassCode(),
                    ""
            );
        } else {
            ToastHelper.show(this, "设备绑定失败");
        }
    }

    /**
     * 绑定设备回调
     *
     * @param event
     */
    public void onEventMainThread(DeviceBindResultEvent event) {
        Timber.e("设备绑定回调");
        dismissDialog();
        if (event.isSuccess()) {
            ToastHelper.show(this, "设备绑定成功");
            //将当前绑定设备设为---默认操作设备
            SettingManager.getInstance(this).setCurrentDid(event.getDid());
            //请求设备列表
            XPGController.getInstance(this).getmCenter().cGetBoundDevices(
                    SettingManager.getInstance(this).getUid(),
                    SettingManager.getInstance(this).getToken());
        } else {
            ToastHelper.show(this, "设备绑定失败");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (currentFragmentState) {
            case MAIN_FRAGMENT:
                getMenuInflater().inflate(R.menu.menu_main, menu);
                break;
            case USER_INFO_FRAGMENT:
                getMenuInflater().inflate(R.menu.menu_user_info, menu);
                break;
            case ABOUT_MODUO_FRAGMENT:
                getMenuInflater().inflate(R.menu.menu_empty, menu);
                break;
            case SHARE_MODUO_FRAGMENT:
                getMenuInflater().inflate(R.menu.menu_share_moduo, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (currentFragmentState) {
            case MAIN_FRAGMENT:
                if (item.getItemId() == R.id.id_menu_add_moduo) {
                    QrCodeUtil.startScan(this);
                } else if (item.getItemId() == R.id.id_menu_share_wifi) {
                    WifiCodeDispActivity.start(this);
                }
                break;
            case USER_INFO_FRAGMENT:
                if (item.getItemId() == R.id.id_menu_edit) {
                    UserInfoEditActivity.start(this);
                }
                break;
            case ABOUT_MODUO_FRAGMENT:
                break;
            case SHARE_MODUO_FRAGMENT:
                if (item.getItemId() == R.id.id_menu_share_moduo) {
                    //// TODO: 2016/1/21 分享魔哆二维码
                    if (XPGController.getCurrentDevice() == null) {
                        ToastHelper.showModuoNotConnected(this);
                    }
                    String picFileName = XPGController.getCurrentDevice().getXpgWifiDevice().getDid() + ".png";
                    if (FileUtil.hasPicture(picFileName)) {
                        FileUtil.sharePicture(this, new File(FileUtil.MODUO_PICTURE_PATH + picFileName));
                    } else {
                        ToastHelper.show(this, "二维码未生成");
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //// TODO: 2016/1/10
        communication.destory();//销毁sdk
        //todo---完全退出程序(若不是跳转到登陆界面)
        if (!isLogOut) {
            android.os.Process.killProcess(android.os.Process.myPid());//确保完全退出，释放所有资源
        }
    }

    @Override
    public void onBackPressed() {
        //判断当前是否为mainFragment
        if (currentFragmentState == FragmentState.MAIN_FRAGMENT) {
            if (System.currentTimeMillis() < (exitTimeInMils + 1500)) {
                super.onBackPressed();
            } else {
                exitTimeInMils = System.currentTimeMillis();
                ToastHelper.show(this, "再次点击退出");
            }
        } else {
            switchFragment(FragmentState.MAIN_FRAGMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取扫描二维码的结果
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QrCodeUtil.parseScanResult(this, result);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.ssthouse.moduo.activity.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.SettingActivity;
import com.ssthouse.moduo.activity.WifiCodeDispActivity;
import com.ssthouse.moduo.activity.account.UserInfoEditActivity;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.QrCodeUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.fragment.sliding.AboutModuoFragment;
import com.ssthouse.moduo.fragment.sliding.IFragmentUI;
import com.ssthouse.moduo.fragment.sliding.ShareModuoFragment;
import com.ssthouse.moduo.fragment.sliding.UserInfoFragment;
import com.ssthouse.moduo.fragment.sliding.main.MainFragment;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面Activity, 控制toolbar和侧边栏
 */
public class MainActivity extends AppCompatActivity implements MainActivityView {
    //点两次退出程序
    private long exitTimeInMils = 0;

    //RequestCode
    public static final int REQUEST_CODE_SWITCH_MODUO = 1000; //SwitchModuoActivity

    //Presenter
    private MainActivityPresenter mPresenter;

    private FragmentManager fragmentManager;
    private MainFragment mainFragment;              //首页
    private UserInfoFragment userInfoFragment;      //个人资料
    private ShareModuoFragment shareDeviceFragment; //分享魔哆
    private AboutModuoFragment aboutModuoFragment;  //关于魔哆

    //当前状态
    public FragmentState currentFragmentState = FragmentState.MAIN_FRAGMENT;

    /**
     * fragment状态切换
     */
    public enum FragmentState {
        SHARE_MODUO_FRAGMENT, ABOUT_MODUO_FRAGMENT, USER_INFO_FRAGMENT, MAIN_FRAGMENT;
    }

    /**
     * 视频对话SDK管理类
     */
    private Communication communication;

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    @Bind(R.id.id_drawer_layout)
    DrawerLayout drawerLayout;

    /**********
     * 侧滑栏
     *************/
    //关闭侧滑栏
    @Bind(R.id.id_iv_close_nav)
    ImageView ivCloseNav;
    //注销
    @Bind(R.id.id_tv_logout)
    TextView tvLogOut;
    //用户头像
    @Bind(R.id.id_iv_avatar)
    ImageView ivAvatar;
    //用户名
    @Bind(R.id.id_tv_username)
    TextView tvUsername;
    //四个板块
    @Bind(R.id.id_menu_main)
    View menuMain;
    @Bind(R.id.id_menu_about_moduo)
    View menuAboutModuo;
    @Bind(R.id.id_menu_user_info)
    View menuUserInfo;
    @Bind(R.id.id_menu_share_moduo)
    View menuShareModuo;
    @Bind(R.id.id_menu_setting)
    View menuSetting;

    //等待dialog
    private View waitDialogView;
    private Dialog waitDialog;

    //确认注销dialog
    private View confirmLogoutDialogView;
    private Dialog confirmLogoutDialog;

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
        ButterKnife.bind(this);

        //Presenter
        mPresenter = new MainActivityPresenter(this, this);

        initView();
        initFragment();
        //初始化视频sdk
        initVideoSdk();
    }

    //初始化视频sdk
    private void initVideoSdk() {
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

    //menu点击事件
    private View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_menu_main:
                    switchFragment(FragmentState.MAIN_FRAGMENT);
                    drawerLayout.closeDrawers();
                    break;
                case R.id.id_menu_user_info:
                    switchFragment(FragmentState.USER_INFO_FRAGMENT);
                    drawerLayout.closeDrawers();
                    break;
                case R.id.id_menu_about_moduo:
                    switchFragment(FragmentState.ABOUT_MODUO_FRAGMENT);
                    drawerLayout.closeDrawers();
                    break;
                case R.id.id_menu_share_moduo:
                    switchFragment(FragmentState.SHARE_MODUO_FRAGMENT);
                    drawerLayout.closeDrawers();
                    break;
                case R.id.id_menu_setting:
                    SettingActivity.start(MainActivity.this);
                    break;
            }
        }
    };

    private void initView() {
        //actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("魔哆");

        //侧滑栏开关响应
        final ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        /****************侧滑栏点击事件*******/
        //用户名   头像>>>点击跳转UserInfoFragment
        View.OnClickListener userInfoCLickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(FragmentState.USER_INFO_FRAGMENT);
                drawerLayout.closeDrawers();
            }
        };
        tvUsername.setOnClickListener(userInfoCLickListener);
        ivAvatar.setOnClickListener(userInfoCLickListener);

        // 注销
        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出dialog
                showConfirmLogoutDialog();
            }
        });
        //关闭侧滑栏
        ivCloseNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        //侧滑栏点击事件
        menuMain.setOnClickListener(menuClickListener);
        menuUserInfo.setOnClickListener(menuClickListener);
        menuAboutModuo.setOnClickListener(menuClickListener);
        menuShareModuo.setOnClickListener(menuClickListener);
        menuSetting.setOnClickListener(menuClickListener);

        //初始化dialog
        waitDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);

        //确认logout dialog
        confirmLogoutDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_msg_confirm, null);
        confirmLogoutDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setView(confirmLogoutDialogView)
                .create();
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    //更新用户名
    private void updateUsername() {
        if (!XPGController.isLogin()) {
            tvUsername.setText("未登录");
            return;
        }
        if (SettingManager.getInstance(this).isAnonymousUser()) {
            tvUsername.setText("匿名登录");
            return;
        }
        tvUsername.setText(SettingManager.getInstance(this).getUserName());
    }

    //刷新UI
    @Override
    public void updateUI() {
        updateUsername();
    }

    @Override
    public void showConfirmLogoutDialog() {
        TextView tvContent = (TextView) confirmLogoutDialogView.findViewById(R.id.id_tv_content);
        tvContent.setText("确认注销?");
        confirmLogoutDialogView.findViewById(R.id.id_iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogoutDialog.dismiss();
            }
        });
        confirmLogoutDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在注销...");
                XPGController.getInstance(MainActivity.this).getmCenter().cLogout();
            }
        });
        confirmLogoutDialog.show();
    }

    @Override
    public void dismissConfirmLogoutDialog() {
        confirmLogoutDialog.dismiss();
    }

    @Override
    public void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissWaitDialog() {
        waitDialog.dismiss();
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
                setTitle("魔哆");
                break;
            case USER_INFO_FRAGMENT:
                currentFragmentState = FragmentState.USER_INFO_FRAGMENT;
                toFragment = userInfoFragment;
                setTitle("个人资料");
                break;
            case ABOUT_MODUO_FRAGMENT:
                currentFragmentState = FragmentState.ABOUT_MODUO_FRAGMENT;
                toFragment = aboutModuoFragment;
                setTitle("关于魔哆");
                break;
            case SHARE_MODUO_FRAGMENT:
                currentFragmentState = FragmentState.SHARE_MODUO_FRAGMENT;
                toFragment = shareDeviceFragment;
                setTitle("生成二维码");
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
                    if (XPGController.getCurrentDevice() == null) {
                        Toast.showModuoNotConnected();
                    }
                    String picFileName = XPGController.getCurrentDevice().getXpgWifiDevice().getDid() + ".png";
                    if (FileUtil.hasPicture(picFileName)) {
                        FileUtil.sharePicture(this, new File(FileUtil.MODUO_QRCODE_PATH + picFileName));
                    } else {
                        Toast.show("二维码未生成");
                    }
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        communication.destroy();//销毁sdk
        //确保完全退出，释放所有资源
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        //判断当前是否为mainFragment
        if (currentFragmentState == FragmentState.MAIN_FRAGMENT) {
            if (System.currentTimeMillis() < (exitTimeInMils + 1500)) {
                super.onBackPressed();
            } else {
                exitTimeInMils = System.currentTimeMillis();
                Toast.show("再次点击退出");
            }
        } else {
            switchFragment(FragmentState.MAIN_FRAGMENT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //切换魔哆回调
        if (requestCode == REQUEST_CODE_SWITCH_MODUO) {
            return;
        }
        //扫描二维码回调
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QrCodeUtil.parseScanResult(this, result);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

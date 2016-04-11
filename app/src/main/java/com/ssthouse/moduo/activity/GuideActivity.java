package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.WifiCodeFragment;
import com.ssthouse.moduo.fragment.account.LoginFragment;
import com.ssthouse.moduo.fragment.gesture.NewGestureFragment;
import com.ssthouse.moduo.model.event.view.GestureLockFinishEvent;
import com.ssthouse.moduo.model.event.view.GuideFinishEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 第一次进入的导航界面{
 *     初始化用户登录
 *     初始化魔哆连接
 * }
 * Created by ssthouse on 2016/1/15.
 */
public class GuideActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    //wifi二维码(给魔哆扫描)
    private WifiCodeFragment wifiCodeFragment;
    //用户登陆fragment
    private LoginFragment loginFragment;
    //新建密码锁fragment
    private NewGestureFragment newGestureFragment;

    //Fragment状态
    State currentState = State.STATE_WIFI_CODE;

    public enum State {
        //wifi二维码
        STATE_WIFI_CODE,
        //登陆
        STATE_LOGIN,
        //手势密码
        STATE_GESTURE_LOCK
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fist_in);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initView();
        initFragment();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle("连接魔哆");
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        wifiCodeFragment = new WifiCodeFragment();
        loginFragment = new LoginFragment();
        newGestureFragment = new NewGestureFragment();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.id_fragment_container, wifiCodeFragment)
                .add(R.id.id_fragment_container, loginFragment)
                .hide(loginFragment)
                .add(R.id.id_fragment_container, newGestureFragment)
                .hide(newGestureFragment)
                .commit();
    }

    //切换fragment
    public void switchFragment(State toState) {
        //隐藏当前fragment
        Fragment currentFragment = null;
        switch (currentState) {
            case STATE_WIFI_CODE:
                currentFragment = wifiCodeFragment;
                break;
            case STATE_LOGIN:
                currentFragment = loginFragment;
                break;
            case STATE_GESTURE_LOCK:
                currentFragment = newGestureFragment;
                break;
        }
        Fragment toFragment = null;
        //更新状态
        currentState = toState;
        switch (toState) {
            case STATE_WIFI_CODE:
                toFragment = wifiCodeFragment;
                setTitle("连接魔哆");
                break;
            case STATE_LOGIN:
                toFragment = loginFragment;
                setTitle("登陆");
                break;
            case STATE_GESTURE_LOCK:
                toFragment = newGestureFragment;
                setTitle("设置图形密码");
                break;
        }
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .hide(currentFragment)
                .show(toFragment)
                .commit();
        //刷新menu
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (currentState) {
            case STATE_WIFI_CODE:
                getMenuInflater().inflate(R.menu.menu_next, menu);
                break;
            case STATE_LOGIN:
                getMenuInflater().inflate(R.menu.menu_empty, menu);
                break;
            case STATE_GESTURE_LOCK:
                getMenuInflater().inflate(R.menu.menu_jump, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.id_menu_jump) {
            switch (currentState) {
                case STATE_WIFI_CODE:
                    switchFragment(State.STATE_LOGIN);
                    break;
                case STATE_LOGIN:
                    exit(true);
                    break;
                case STATE_GESTURE_LOCK:
                    exit(true);
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //手势密码编辑完成
    public void onEventMainThread(GestureLockFinishEvent event) {
        exit(true);
    }

    //退出
    private void exit(boolean isSuccess) {
        EventBus.getDefault().post(new GuideFinishEvent(isSuccess));
        finish();
    }

    @Override
    public void onBackPressed() {
        exit(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package com.ssthouse.moduo.activity.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.CmdCenter;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.fragment.gesture.ConfirmGestureFragment;
import com.ssthouse.moduo.fragment.gesture.EmptyFragment;
import com.ssthouse.moduo.fragment.gesture.NewGestureFragment;
import com.ssthouse.moduo.model.event.view.GestureLockFinishEvent;
import com.ssthouse.moduo.model.event.xpg.GetXpgUserInfoEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 简介:  手势密码控制Activity
 * 功能:  初始
 * 包含的fragment{
 *      EmptyFragment: 用于提示用户正在加载
 *      NewGestureFragment: 新建手势密码
 *      ConfirmGestureFragment: 验证手势密码是否正确, 正确后跳转至
 * }
 *
 * Created by ssthouse
 */

public class GestureLockActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private EmptyFragment emptyFragment;
    private NewGestureFragment newGestureFragment;
    private ConfirmGestureFragment confirmGestureFragment;

    private Dialog confirmDialog;
    private View confirmDialogView;

    public static void start(Context context) {
        Intent intent = new Intent(context, GestureLockActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        initView();
        initDialog();
        initFragment();

        SettingManager settingManager = SettingManager.getInstance(this);
        //未登录 或 匿名登录 退出
        if (!settingManager.isLogined()) {
            showConfirmDialog("请登录后设置图形密码");
            return;
        }
        if (settingManager.isAnonymousUser()) {
            showConfirmDialog("当前为匿名登录");
            return;
        }
        //无网络连接   退出
        if (!NetUtil.isConnected(this)) {
            showConfirmDialog("当前无网络连接");
            return;
        }

        //获取当前登录用户数据
        CmdCenter.getInstance(this).getXPGWifiSDK().getUserInfo(settingManager.getToken());
    }

    public void toNewGestureFragment() {
        setTitle("新手势");
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    public void toEditGestureFragment() {
        setTitle("验证手势");
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.id_fragment_container, confirmGestureFragment)
                .commit();
    }

    private void initFragment() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        emptyFragment = new EmptyFragment();
        newGestureFragment = new NewGestureFragment();
        confirmGestureFragment = new ConfirmGestureFragment();

        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.id_fragment_container, emptyFragment)
                .commit();
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("新建图形密码");
    }

    private void initDialog() {
        confirmDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_wait_confirm, null);
        confirmDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .setCancelable(false)
                .create();
    }

    //显示确认退出Dialog
    private void showConfirmDialog(String msg) {
        //提示文字
        TextView tvExit = (TextView) confirmDialogView.findViewById(R.id.id_tv_content);
        tvExit.setText(msg);
        //点击事件---确认退出
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        };
        confirmDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(clickListener);
        confirmDialogView.findViewById(R.id.id_iv_close).setOnClickListener(clickListener);
        confirmDialog.show();
    }

    //图形密码编辑完成回调
    public void onEventMainThread(GestureLockFinishEvent event) {
        if (!ActivityUtil.isTopActivity(this, "GestureLockActivity")) {
            return;
        }
        finish();
    }

    //获取XpgUserInfo回调
    public void onEventMainThread(GetXpgUserInfoEvent event) {
        if(event.isSuccess()){
            String gestureStr = event.getXpgUserInfo().getRemark();
            //Timber.e(gestureStr+":\t长度为:"+gestureStr.length());
            //判断手势密码是否为空
            if (gestureStr.length() == 0) {
                Toast.show("初始化图形密码");
                toNewGestureFragment();
            }else {
                SettingManager.getInstance(this).setGestureLock(gestureStr);
                toEditGestureFragment();
            }
        }else{
            showConfirmDialog("获取用户手势密码失败, 请稍后重试.");
        }
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

package com.ssthouse.moduo.main.view.activity.account;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.view.fragment.gesture.EditGestureFragment;
import com.ssthouse.moduo.main.view.fragment.gesture.NewGestureFragment;

/**
 * 手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class GestureLockActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private NewGestureFragment newGestureFragment;
    private EditGestureFragment editGestureFragment;

    private MaterialDialog exitDialog;

    public static void start(Context context) {
        Intent intent = new Intent(context, GestureLockActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);

        initView();

        initFragment();

        //未登录 或 匿名登录 退出
        if (!SettingManager.getInstance(this).isLogined()) {
            showExitDialog("请先登录后设置图形密码");
            return;
        }
        if (SettingManager.getInstance(this).isAnonymousUser()) {
            showExitDialog("当前为匿名登录");
            return;
        }
        //初始fragment切换
        if (SettingManager.getInstance(this).getGestureLock() == null
                || SettingManager.getInstance(this).getGestureLock().length() == 0) {
            toNewGestureFragment();
        } else {
            toEditGestureFragment();
        }
    }

    public void toNewGestureFragment() {
        getSupportActionBar().setTitle("新建图形密码");
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    public void toEditGestureFragment() {
        getSupportActionBar().setTitle("修改图形密码");
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, editGestureFragment)
                .commit();
    }

    private void initFragment() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        newGestureFragment = new NewGestureFragment();
        editGestureFragment = new EditGestureFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("新建图形密码");

        exitDialog = new MaterialDialog.Builder(this)
                .customView(R.layout.dialog_exit, true)
                .autoDismiss(true)
                .positiveText("确定")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .build();
    }

    private void showExitDialog(String msg) {
        TextView tvExit = (TextView) exitDialog.getCustomView().findViewById(R.id.id_tv_exit);
        tvExit.setText(msg);
        exitDialog.show();
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
}

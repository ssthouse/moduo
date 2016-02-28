package com.ssthouse.moduo.activity.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.fragment.gesture.EditGestureFragment;
import com.ssthouse.moduo.fragment.gesture.NewGestureFragment;
import com.ssthouse.moduo.model.event.view.GestureLockFinishEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class GestureLockActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private NewGestureFragment newGestureFragment;
    private EditGestureFragment editGestureFragment;

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
        initFragment();

        //未登录 或 匿名登录 退出
        if (!SettingManager.getInstance(this).isLogined()) {
            showConfirmDialog("请登录后设置图形密码");
            return;
        }
        if (SettingManager.getInstance(this).isAnonymousUser()) {
            showConfirmDialog("当前为匿名登录");
            return;
        }
    }

    public void toNewGestureFragment() {
        setTitle("新建图形密码");
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    public void toEditGestureFragment() {
        setTitle("修改图形密码");
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

        //初始fragment切换
        if (SettingManager.getInstance(this).getGestureLock() == null
                || SettingManager.getInstance(this).getGestureLock().length() == 0) {
            toNewGestureFragment();
        } else {
            toEditGestureFragment();
        }
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

        confirmDialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_confirm, null);
        confirmDialog = new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .setCancelable(false)
                .create();
    }

    private void showConfirmDialog(String msg) {
        //提示文字
        TextView tvExit = (TextView) confirmDialogView.findViewById(R.id.id_tv_content);
        tvExit.setText(msg);
        //点击事件---确认退出
        confirmDialogView.findViewById(R.id.id_tv_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
        confirmDialog.show();
    }

    //图形密码编辑完成回调
    public void onEventMainThread(GestureLockFinishEvent event) {
        if (!ActivityUtil.isTopActivity(this, "GestureLockActivity")) {
            return;
        }
        finish();
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

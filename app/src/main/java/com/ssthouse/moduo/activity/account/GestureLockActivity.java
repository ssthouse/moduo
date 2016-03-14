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

import com.avos.avoscloud.AVObject;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.fragment.gesture.EditGestureFragment;
import com.ssthouse.moduo.fragment.gesture.EmptyFragment;
import com.ssthouse.moduo.fragment.gesture.NewGestureFragment;
import com.ssthouse.moduo.model.event.view.GestureLockFinishEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 手势密码
 * 正常登录的话---本地的手势密码应该是正确的
 * Created by ssthouse on 2016/1/16.
 */
public class GestureLockActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private EmptyFragment emptyFragment;
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
        initDialog();
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

        //尝试获取云端服务器的密码---获取失败也是直接退出---成功才跳转到验证Fragment
        CloudUtil.getUserInfoObject(SettingManager.getInstance(this).getUserName())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AVObject>() {
                    @Override
                    public void call(AVObject avObject) {
                        if (avObject == null) {
                            showConfirmDialog("获取用户密码锁信息失败, 请稍后重试");
                        } else {
                            //将密码数据更新到本地
                            String gestureLock = (String) avObject.get(CloudUtil.KEY_GESTURE_PASSWORD);
                            SettingManager.getInstance(GestureLockActivity.this).setGestureLock(gestureLock);
                            //切换fragment
                            toEditGestureFragment();
                        }
                    }
                });
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
                .replace(R.id.id_fragment_container, editGestureFragment)
                .commit();
    }

    private void initFragment() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        emptyFragment = new EmptyFragment();
        newGestureFragment = new NewGestureFragment();
        editGestureFragment = new EditGestureFragment();

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

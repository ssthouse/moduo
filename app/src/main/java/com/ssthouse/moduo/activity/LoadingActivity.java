package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.main.MainActivity;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.event.view.AppIntroFinishEvent;
import com.ssthouse.moduo.model.bean.event.xpg.XPGLoginResultEvent;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * loading界面:
 * 程序入口
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     * 启动当前activity
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoadingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        //是否第一次
        if (SettingManager.getInstance(this).isFistIn()) {
            SettingManager.getInstance(this).setIsFistIn(false);
            AppIntroActivity.start(this);
            return;
        }

        //尝试登陆
        tryLogin();
    }

    //尝试登陆
    private void tryLogin() {
        //判断是否为匿名登录
        if (SettingManager.getInstance(this).isAnonymousUser()) {
            XPGController.getInstance(this).getmCenter().cLoginAnonymousUser();
            Timber.e("匿名登录");
        } else {
            XPGController.getInstance(this).getmCenter().cLogin(
                    SettingManager.getInstance(this).getUserName(),
                    SettingManager.getInstance(this).getPassword()
            );
            Timber.e("实名登陆");
        }
    }

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            Timber.e("机智云---登录成功");
            XPGController.setLogin(true);
            //更新本地用户数据
            CloudUtil.updateUserInfoToLocal(this, SettingManager.getInstance(this).getUserName());
            //保存机智云登陆数据
            SettingManager.getInstance(this).setLoginCacheInfo(event);
            //跳转Activity
            MainActivity.start(this);
            finish();
        } else {
            Timber.e("机智云---登录失败");
            XPGController.setLogin(false);
            MainActivity.start(this);
            finish();
        }
    }

    /**
     * 如果是第一次进入---会先启动AppIntroActivity然后发出完成介绍的event
     * @param event
     */
    public void onEventMainThread(AppIntroFinishEvent event){
        if(event.isSuccess()) {
            tryLogin();
        }else{
            //介绍失败---直接退出
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

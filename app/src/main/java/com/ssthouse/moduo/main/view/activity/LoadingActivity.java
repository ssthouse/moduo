package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.cons.Constant;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;

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

        //// 测试注册登陆逻辑
//        if (StringUtils.isEmpty(SettingManager.getInstance(this).getUid())
//                || StringUtils.isEmpty(SettingManager.getInstance(this).getToken())) {
//            RegisterActivity.start(this);
//        }

        //匿名登录
        XPGController.getInstance(this).getmCenter().cLoginAnonymousUser();
    }

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(this, "LoadingActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //改变全局登陆状态
            Constant.isXpgLogin = true;
            Timber.e("机智云---登录成功");
            ToastHelper.show(this, "登陆成功!");
            //保存机智云登陆数据
            SettingManager.getInstance(this).setLoginInfo(event);
            //跳转Activity
            MainActivity.start(this, true);
            finish();
        } else {
            MainActivity.start(this, false);
            Timber.e("机智云---登录失败");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

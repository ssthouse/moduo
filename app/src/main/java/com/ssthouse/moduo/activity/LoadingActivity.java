package com.ssthouse.moduo.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.main.MainActivity;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.view.AppIntroFinishEvent;
import com.ssthouse.moduo.model.event.view.GuideFinishEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;

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
            GuideActivity.start(this);
            return;
        }

        //启动动画
        final ImageView ivBg = (ImageView) findViewById(R.id.id_iv_bg);
        ValueAnimator animator = ObjectAnimator.ofFloat(ivBg, "scaleX", 1f, 1.1f);
        animator.setDuration(1500);
        ValueAnimator animator1 = ObjectAnimator.ofFloat(ivBg, "scaleY", 1f, 1.1f);
        final AnimatorSet animatorSet = new AnimatorSet();
        animator1.setDuration(1500);
        animatorSet.play(animator).with(animator1);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                jump2Main();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.start();
    }

    //跳转MainActivity
    private void jump2Main() {
        MainActivity.start(LoadingActivity.this);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    /**
     * 机智云---登录成功回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(this, "LoadingActivity")) {
            Timber.e("LoadingActivity is not in the top!");
            return;
        }
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

    //AppIntroActivity完成介绍的event
    public void onEventMainThread(AppIntroFinishEvent event) {
        if (event.isSuccess()) {
            jump2Main();
        } else {
            //介绍失败---直接退出
            finish();
        }
    }

    //GuideActivity完成第一次介绍后回调事件
    public void onEventMainThread(GuideFinishEvent event) {
        jump2Main();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

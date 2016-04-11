package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.model.event.view.AppIntroFinishEvent;

import de.greenrobot.event.EventBus;

/**
 * App介绍Activity(暂时未用到)
 * Created by ssthouse on 2016/1/17.
 */
public class AppIntroActivity extends AppIntro {

    public static void start(Context context) {
        Intent intent = new Intent(context, AppIntroActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        addSlide(AppIntroFragment.newInstance("Page1", "第一个介绍页面", R.drawable.intro1, R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance("Page2", "第二个介绍页面", R.drawable.intro2, R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance("Page3", "第三个介绍页面", R.drawable.intro3, R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance("Page4", "第四个介绍页面", R.drawable.intro4, R.color.colorPrimary));
        //减少下方按钮
        showSkipButton(false);
        setProgressButtonEnabled(false);
        //震动
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        EventBus.getDefault().post(new AppIntroFinishEvent(true));
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        finish();
        Toast.show( "完成app介绍");
        EventBus.getDefault().post(new AppIntroFinishEvent(true));
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new AppIntroFinishEvent(false));
    }
}

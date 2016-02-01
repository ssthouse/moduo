package com.ssthouse.moduo.moduo.model;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import timber.log.Timber;

/**
 * Created by ssthouse on 2016/1/24.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        ActiveAndroid.initialize(this);
        //讯飞
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=56a6efef");
    }
}
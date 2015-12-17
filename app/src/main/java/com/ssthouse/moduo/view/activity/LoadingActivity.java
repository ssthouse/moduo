package com.ssthouse.moduo.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.orhanobut.logger.Logger;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.video.ViewerLoginResultEvent;

import de.greenrobot.event.EventBus;

/**
 * loading界面
 * Created by ssthouse on 2015/12/17.
 */
public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        EventBus.getDefault().register(this);

        loadSdkLib();

        Communication.getInstance(this);
    }

    //load sdk lib
    private void loadSdkLib() {
        System.loadLibrary("gnustl_shared");
        System.loadLibrary("ffmpeg");
        System.loadLibrary("avdecoder");
        System.loadLibrary("sdk30");
        System.loadLibrary("viewer30");
    }

    public void onEventMainThread(ViewerLoginResultEvent event){
        if(event.isSuccess()){
            Logger.e("转向MainActivity");
            MainActivity.start(this);
            finish();
        }else{
            ToastHelper.show(this, "登陆视频sdk失败");
        }
    }
}

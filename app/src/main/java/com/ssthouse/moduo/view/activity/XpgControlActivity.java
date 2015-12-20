package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ssthouse.moduo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * xpg设备控制activity:
 *
 * 控制唯一单例XPGManager里面的currentDevice
 * Created by ssthouse on 2015/12/20.
 */
public class XpgControlActivity extends AppCompatActivity {

    /**
     * 温度
     */
    @Bind(R.id.id_sb_temperature)
    SeekBar sbTemperature;

    /**
     * 湿度
     */
    @Bind(R.id.id_tv_humidity)
    TextView tvHumidity;

    /**
     * 启动当前activity
     * @param context
     */
    public static void start(Context context){
        Intent intent = new Intent(context, XpgControlActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xpg_control);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
    }

    //todo
    public void onEventMainThread(String s){

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

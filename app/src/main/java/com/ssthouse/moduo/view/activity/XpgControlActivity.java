package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.model.DeviceData;
import com.ssthouse.moduo.model.event.setting.DeviceDataChangedEvent;
import com.ssthouse.moduo.model.event.setting.GetDeviceDataEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * xpg设备控制activity:
 * <p/>
 * 控制唯一单例XPGManager里面的currentDevice
 * Created by ssthouse on 2015/12/20.
 */
public class XpgControlActivity extends AppCompatActivity {

    /**
     * 保存当前的设备数据
     */
    private DeviceData deviceData;

    /**
     * 温度
     */
    @Bind(R.id.id_sb_temperature)
    SeekBar sbTemperature;

    /**
     * 温度显示
     */
    @Bind(R.id.id_tv_temperature)
    TextView tvTemperature;

    /**
     * 湿度
     */
    @Bind(R.id.id_tv_humidity)
    TextView tvHumidity;

    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context
     * @param deviceData 设备初始数据
     */
    public static void start(Context context, DeviceData deviceData) {
        Intent intent = new Intent(context, XpgControlActivity.class);
        intent.putExtra("device_data", deviceData);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xpg_control);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        //获得初始设备数据
        deviceData = (DeviceData) getIntent().getSerializableExtra("device_data");

        initView();
    }

    private void initView() {
        //toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("控制台");

        //温度显示
        tvTemperature.setText(deviceData.getTemperature() + "℃");

        //温度调节监听
        sbTemperature.setProgress(deviceData.getTemperature() - 10);
        sbTemperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //只有用户滑动才会触发
                if (fromUser) {
                    int temperature = progress + 10;
                    tvTemperature.setText(temperature + "℃");
                    //TODO---尝试修改数据
                    //showWaitChangeTemperature();
                    //发出数据修改请求
                    XPGController.getInstance(XpgControlActivity.this).getmCenter().cWrite(
                            XPGController.getCurrentXpgWifiDevice(),
                            DeviceData.DeviceCons.KEY_TEMPERATURE,
                            temperature
                    );
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //湿度
        tvHumidity.setText(deviceData.getHumidity() + "");

        //初始化等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 显示等待调节温度dialog
     */
    private void showWaitChangeTemperature() {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText("正在设置温度");
        waitDialog.show();
    }

    /**
     * xpg设备操作结果回调
     *
     * @param event
     */
    public void onEventMainThread(GetDeviceDataEvent event) {
        if (event.isSuccess()) {
            //操作成功---更新数据
            deviceData.setTemperature(event.getInitDeviceData().getTemperature() + 10);
            Timber.e("设备数据设置成功");
        } else {
            ToastHelper.show(this, "数据设置失败");
            sbTemperature.setProgress(deviceData.getTemperature() - 10);
            tvTemperature.setText(deviceData.getTemperature() + "℃");
        }
        waitDialog.dismiss();
    }

    /**
     * xpg设备自己推送过来的数据
     *
     * @param event
     */
    public void onEventMainThread(DeviceDataChangedEvent event) {
        //更新数据
        deviceData.setTemperature(event.getChangedDeviceData().getTemperature());
        //更新UI
        tvTemperature.setText("" + event.getChangedDeviceData().getTemperature()+"℃");
        sbTemperature.setProgress(event.getChangedDeviceData().getTemperature() - 10);
        tvHumidity.setText("" + event.getChangedDeviceData().getHumidity());
        //toast提示
        ToastHelper.show(this, "设备数据更新");
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

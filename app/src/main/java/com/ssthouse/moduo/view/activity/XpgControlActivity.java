package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.control.util.ByteUtils;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.model.DeviceData;
import com.ssthouse.moduo.model.event.xpg.DeviceDataChangedEvent;
import com.ssthouse.moduo.model.event.xpg.GetDeviceDataEvent;

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

    private static final int OFFSET = 100;
    private static final int X_HEAD_OFFSET = 180;
    private static final int Y_HEAD_OFFSET = 90;
    private static final int Z_HEAD_OFFSET = 90;
    private static final int BODY_OFFSET = 100;
    private static final int MAX = 100;

    /**
     * 保存当前的设备数据
     */
    private DeviceData deviceData;
    /**
     * 温度显示 temperature
     */
    @Bind(R.id.id_tv_temperature)
    TextView tvTemperature;
    /**
     * 湿度 humidity
     */
    @Bind(R.id.id_tv_humidity)
    TextView tvHumidity;
    /**
     * 亮度 luminance
     */
    @Bind(R.id.id_tv_luminance)
    TextView tvLuminance;
    /**
     * 电量
     */
    @Bind(R.id.id_tv_power)
    TextView tvPower;
    /**
     * 硬件版本
     */
    @Bind(R.id.id_tv_hw_version)
    TextView tvHwVersion;
    /**
     * 软件版本
     */
    @Bind(R.id.id_tv_sw_version)
    TextView tvSwVersion;
    /**
     * 机器人身体的控制
     */
    @Bind(R.id.id_tv_x_head)
    TextView tvXHead;
    @Bind(R.id.id_sb_x_head)
    SeekBar sbXHead;
    @Bind(R.id.id_tv_y_head)
    TextView tvYHead;
    @Bind(R.id.id_sb_y_head)
    SeekBar sbYHead;
    @Bind(R.id.id_tv_z_head)
    TextView tvZHead;
    @Bind(R.id.id_sb_z_head)
    SeekBar sbZHead;
    @Bind(R.id.id_tv_x_body)
    TextView tvXBody;
    @Bind(R.id.id_sb_x_body)
    SeekBar sbXBody;
    @Bind(R.id.id_tv_y_body)
    TextView tvYBody;
    @Bind(R.id.id_sb_y_body)
    SeekBar sbYBody;
    @Bind(R.id.id_tv_z_body)
    TextView tvZBody;
    @Bind(R.id.id_sb_z_body)
    SeekBar sbZBody;
    /**
     * 等待dialog
     */
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

        //todo---尝试获取设备数据
        XPGController.getCurrentXpgWifiDevice().setListener(XPGController.getInstance(this).getDeviceListener());
       // Timber.e(XPGController.getCurrentXpgWifiDevice().getDid() + "\t请求数据!!!!");

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        XPGController.getInstance(this).getmCenter().cGetStatus(XPGController.getCurrentXpgWifiDevice());
        Timber.e(XPGController.getCurrentXpgWifiDevice().getDid() + "\t请求数据!!!!");
    }

    private void initView() {
        //toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("控制台");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化设备数据展示
        tvTemperature.setText("" + deviceData.getTemperature() + "℃");
        tvHumidity.setText("" + deviceData.getHumidity());
        tvLuminance.setText("" + deviceData.getLuminance());
        tvPower.setText("" + deviceData.getPower());

        tvHwVersion.setText(ByteUtils.bytes2HexString(deviceData.getHwVersion()));
        tvSwVersion.setText(ByteUtils.bytes2HexString(deviceData.getSwVersion()));

        tvXHead.setText("" + deviceData.getxHead());
        sbXHead.setMax(X_HEAD_OFFSET * 2);
        sbXHead.setProgress(deviceData.getxHead() + X_HEAD_OFFSET);
        sbXHead.setOnSeekBarChangeListener(onSeekBarChangeListener);
        tvYHead.setText("" + deviceData.getyHead());
        sbYHead.setMax(Y_HEAD_OFFSET * 2);
        sbYHead.setProgress(deviceData.getyHead() + Y_HEAD_OFFSET);
        sbYHead.setOnSeekBarChangeListener(onSeekBarChangeListener);
        tvZHead.setText("" + deviceData.getzHead());
        sbZHead.setMax(Z_HEAD_OFFSET * 2);
        sbZHead.setProgress(deviceData.getzHead() + Z_HEAD_OFFSET);
        sbZHead.setOnSeekBarChangeListener(onSeekBarChangeListener);

        tvXBody.setText("" + deviceData.getxBody());
        sbXBody.setMax(BODY_OFFSET * 2);
        sbXBody.setProgress(deviceData.getxBody() + BODY_OFFSET);
        sbXBody.setOnSeekBarChangeListener(onSeekBarChangeListener);
        tvYBody.setText("" + deviceData.getyBody());
        sbYBody.setMax(BODY_OFFSET * 2);
        sbYBody.setProgress(deviceData.getyBody() + BODY_OFFSET);
        sbYBody.setOnSeekBarChangeListener(onSeekBarChangeListener);
        tvZBody.setText("" + deviceData.getzBody());
        sbZBody.setMax(BODY_OFFSET * 2);
        sbZBody.setProgress(deviceData.getzBody() + BODY_OFFSET);
        sbZBody.setOnSeekBarChangeListener(onSeekBarChangeListener);

        //初始化等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 滑动条的监听器
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                String key = "";
                switch (seekBar.getId()) {
                    case R.id.id_sb_x_head:
                        key = DeviceData.DeviceCons.KEY_X_HEAD;
                        progress -= X_HEAD_OFFSET;
                        break;
                    case R.id.id_sb_y_head:
                        key = DeviceData.DeviceCons.KEY_Y_HEAD;
                        progress -= Y_HEAD_OFFSET;
                        break;
                    case R.id.id_sb_z_head:
                        key = DeviceData.DeviceCons.KEY_Z_HEAD;
                        progress -= Z_HEAD_OFFSET;
                        break;
                    case R.id.id_sb_x_body:
                        key = DeviceData.DeviceCons.KEY_X_BODY;
                        progress -= BODY_OFFSET;
                        break;
                    case R.id.id_sb_y_body:
                        key = DeviceData.DeviceCons.KEY_Y_BODY;
                        progress -= BODY_OFFSET;
                        break;
                    case R.id.id_sb_z_body:
                        key = DeviceData.DeviceCons.KEY_Z_BODY;
                        progress -= BODY_OFFSET;
                        break;
                }
                //发出数据修改请求
                XPGController.getInstance(XpgControlActivity.this).getmCenter().cWrite(
                        XPGController.getCurrentXpgWifiDevice(),
                        key,
                        progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /**
     * 显示等待调节温度dialog
     */
    private void showWait(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    /**
     * todo 目前来看---设备收到数据变化后根本就没有回调这个方法
     * xpg设备操作结果回调
     *
     * @param event
     */
    public void onEventMainThread(GetDeviceDataEvent event) {
        if (!ActivityUtil.isTopActivity(this, "XpgControlActivity")) {
            return;
        }
        if (event.isSuccess()) {
            //更新数据
            deviceData = event.getDeviceData();
            //操作成功---更新数据
            updateUI();
            Timber.e("设备数据设置成功");
        } else {
            ToastHelper.show(this, "数据设置失败");
            Timber.e("设备数据设置失败");
            //还原原数据UI
            updateUI();
        }
        waitDialog.dismiss();
    }

    /**
     * xpg设备自己推送过来的数据
     *
     * @param event
     */
    public void onEventMainThread(DeviceDataChangedEvent event) {
        //更新当前数据
        deviceData = event.getChangedDeviceData();
        //更新UI
        updateUI();
        //toast提示
        ToastHelper.show(this, "设备数据更新");
    }

    /**
     * 更新UI
     */
    private void updateUI() {
        //更新UI
        //初始化设备数据展示
        tvTemperature.setText("" + deviceData.getTemperature() + "℃");
        tvHumidity.setText("" + deviceData.getHumidity());
        tvLuminance.setText("" + deviceData.getLuminance());
        tvPower.setText("" + deviceData.getPower());

        tvHwVersion.setText(ByteUtils.bytes2HexString(deviceData.getHwVersion()));
        tvSwVersion.setText(ByteUtils.bytes2HexString(deviceData.getSwVersion()));

        tvXHead.setText("" + deviceData.getxHead());
        sbXHead.setProgress(deviceData.getxHead() + X_HEAD_OFFSET);
        tvYHead.setText("" + deviceData.getyHead());
        sbYHead.setProgress(deviceData.getyHead() + Y_HEAD_OFFSET);
        tvZHead.setText("" + deviceData.getzHead());
        sbZHead.setProgress(deviceData.getzHead() + Z_HEAD_OFFSET);

        tvXBody.setText("" + deviceData.getxBody());
        sbXBody.setProgress(deviceData.getxBody() + BODY_OFFSET);
        tvYBody.setText("" + deviceData.getyBody());
        sbYBody.setProgress(deviceData.getyBody() + BODY_OFFSET);
        tvZBody.setText("" + deviceData.getzBody());
        sbZBody.setProgress(deviceData.getzBody() + BODY_OFFSET);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                XPGController.getCurrentXpgWifiDevice().disconnect();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        XPGController.getCurrentXpgWifiDevice().disconnect();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

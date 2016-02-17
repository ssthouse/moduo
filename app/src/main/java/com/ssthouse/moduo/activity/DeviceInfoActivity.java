package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.FileUtil;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.QrCodeUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.xpg.UnbindResultEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 显示设备信息的activity
 * Created by ssthouse on 2015/12/23.
 */
public class DeviceInfoActivity extends AppCompatActivity {

    /**
     * 当前处理设备
     */
    private XPGWifiDevice mXpgWifiDevice;

    @Bind(R.id.id_tv_cid_number)
    TextView tvCidNumber;

    @Bind(R.id.id_tv_username)
    TextView tvUsername;

    @Bind(R.id.id_tv_password)
    TextView tvPassword;

    @Bind(R.id.id_tv_did)
    TextView tvDid;

    @Bind(R.id.id_tv_passcode)
    TextView tvPasscode;

    @Bind(R.id.id_tv_product_key)
    TextView tvProductKey;

    @Bind(R.id.id_tv_ip)
    TextView tvIp;

    @Bind(R.id.id_tv_mac)
    TextView tvMac;

    //删除设备按钮
    @Bind(R.id.id_btn_delete_device)
    Button btnDeleteDevice;

    //分享设备按钮
    @Bind(R.id.id_btn_share_device)
    Button btnShareDevice;

    /**
     * 启动当前activity:
     * 启动前应该讲XPGController中的currentDevice初始化
     *
     * @param context
     */
    public static void start(Context context) {
        if (XPGController.getCurrentDevice() == null) {
            return;
        }
        Intent intent = new Intent(context, DeviceInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        mXpgWifiDevice = XPGController.getCurrentDevice().getXpgWifiDevice();

        initView();
    }

    private void initView() {
        //toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("设备信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //视频参数
        String cidNumber = PreferenceHelper.getInstance(this).getCidNumber(mXpgWifiDevice.getDid());
        tvCidNumber.setText("" + cidNumber);
        String username = PreferenceHelper.getInstance(this).getUsername(mXpgWifiDevice.getDid());
        tvUsername.setText(username);
        String password = PreferenceHelper.getInstance(this).getPassword(mXpgWifiDevice.getDid());
        tvPassword.setText(password);
        //设备参数配置
        tvDid.setText(mXpgWifiDevice.getDid());
        tvProductKey.setText(mXpgWifiDevice.getProductKey());
        tvPasscode.setText(mXpgWifiDevice.getPasscode());
        //网络参数
        tvIp.setText(mXpgWifiDevice.getIPAddress());
        tvMac.setText(mXpgWifiDevice.getMacAddress());

        //删除当前选中设备
        btnDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("删除当前设备");
                //获取当前设备
                XPGWifiDevice device = XPGController.getCurrentDevice().getXpgWifiDevice();
                //解除绑定当前设备
                XPGController.getInstance(DeviceInfoActivity.this).getmCenter().cUnbindDevice(
                        SettingManager.getInstance(DeviceInfoActivity.this).getUid(),
                        SettingManager.getInstance(DeviceInfoActivity.this).getToken(),
                        device.getDid(),
                        device.getPasscode()
                );
            }
        });

        //分享当前设备
        btnShareDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("分享当前设备二维码");
                //获取当前设备
                XPGWifiDevice device = XPGController.getCurrentDevice().getXpgWifiDevice();
                //获取分享的bitmap
                PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(DeviceInfoActivity.this);
                String cid = preferenceHelper.getCidNumber(device.getDid());
                String username = preferenceHelper.getUsername(device.getDid());
                String password = preferenceHelper.getPassword(device.getDid());
                String content = QrCodeUtil.getDeviceQrCodeContent(device.getProductKey(), device.getDid(),
                        device.getPasscode(), cid + "", username, password);
                Bitmap bitmap = QrCodeUtil.generateQRCode(content);
                File bitmapFile = new File(FileUtil.saveBitmap(DeviceInfoActivity.this, bitmap, "test"));
                //将照片保存到本地
                //使用intent分享照片
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                Uri uri = Uri.fromFile(bitmapFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                //开启分享
                startActivity(shareIntent);
            }
        });
    }

    /**
     * 点击删除设备的回调
     *
     * @param event
     */
    public void onEventMainThread(UnbindResultEvent event) {
        if (event.isSuccess()) {
            ToastHelper.show(this, "设备解绑成功");
        } else {
            ToastHelper.show(this, "设备解绑失败");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

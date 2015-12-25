package com.ssthouse.moduo.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.model.event.NetworkStateChangeEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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


    /**
     * 启动当前activity:
     * 启动前应该讲XPGController中的currentDevice初始化
     *
     * @param context
     */
    public static void start(Context context) {
        if (XPGController.getCurrentXpgWifiDevice() == null) {
            return;
        }
        Intent intent = new Intent(context, DeviceInfoActivity.class);
        context.startActivity(intent);
    }


    /**
     * todo
     * @param s
     */
    public void onEventMainThread(NetworkStateChangeEvent s){

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        mXpgWifiDevice = XPGController.getCurrentXpgWifiDevice();

        initView();
    }

    private void initView() {
        //toolbar
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("设备信息");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //视频参数
        long cidNumber = PreferenceHelper.getInstance(this).getCidNumber(mXpgWifiDevice.getDid());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

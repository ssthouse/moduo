package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.AddDeviceFragment;
import com.ssthouse.moduo.fragment.DeviceListFragment;

/**
 * 家居控制Activity, 控制家具的添加和删除(暂时未用)
 * 最好定义一个枚举----表示当前activity的状态
 * Created by ssthouse on 2016/1/13.
 */
public class HomeControlActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private AddDeviceFragment addDeviceFragment;
    private DeviceListFragment deviceListFragment;


    public static void start(Context context) {
        Intent intent = new Intent(context, HomeControlActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_control);

        initView();
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        addDeviceFragment = new AddDeviceFragment();
        deviceListFragment = new DeviceListFragment();
        switchFragment(addDeviceFragment);
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("家电控制");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 切换fragment
     * @param toFragment
     */
    private void switchFragment(Fragment toFragment){
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, toFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

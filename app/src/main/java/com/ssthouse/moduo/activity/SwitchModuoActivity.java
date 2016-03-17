package com.ssthouse.moduo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.moduoswitch.SwitchModuoFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 切换魔哆Activity
 * Created by ssthouse on 2016/2/29.
 */
public class SwitchModuoActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    //本地魔哆是否切换的标识符---默认没有变化
    private boolean isModuoSwitched = false;

    private FragmentManager mFragmentManager;
    private SwitchModuoFragment switchModuoFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, SwitchModuoActivity.class);
        context.startActivity(intent);
    }

    public static void startForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, SwitchModuoActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_moduo);
        ButterKnife.bind(this);

        initView();
        initFragment();
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        switchModuoFragment = new SwitchModuoFragment();
        mFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.id_fragment_container, switchModuoFragment)
                .commit();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setTitle("切换魔哆");
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if (isModuoSwitched) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    //getter*************************************setter******************************
    public boolean isModuoSwitched() {
        return isModuoSwitched;
    }

    public void setIsModuoSwitched(boolean isModuoSwitched) {
        this.isModuoSwitched = isModuoSwitched;
    }
}

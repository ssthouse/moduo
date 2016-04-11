package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.WifiCodeFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 展示手机wifi生成的二维码的Activity
 * Created by ssthouse on 2016/1/13.
 */
public class WifiCodeDispActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    public static void start(Context context) {
        Intent intent = new Intent(context, WifiCodeDispActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_code_disp);
        ButterKnife.bind(this);
        initView();

        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_fragment_container, new WifiCodeFragment())
                .commit();
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        setTitle("连接绑定");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }
}

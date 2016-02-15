package com.ssthouse.moduo.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.fragment.WifiCodeFragment;

/**
 * 手机连接的wifi形成二维码---供魔哆扫描
 * Created by ssthouse on 2016/1/13.
 */
public class WifiCodeDispActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, WifiCodeDispActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_code_disp);

        initView();

        initFragment();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_fragment_container, new WifiCodeFragment())
                .commit();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("连接绑定");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.id_menu_pass:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pass, menu);
        return true;
    }
}

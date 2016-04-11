package com.ssthouse.moduo.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.video.CallingFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 拨号Activity
 * Created by ssthouse on 2016/2/1.
 */
public class CallingActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private CallingFragment callingFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, CallingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_calling);
        ButterKnife.bind(this);
        initView();
        initFragment();
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        setTitle("连线魔哆");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        callingFragment = new CallingFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, callingFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
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
}

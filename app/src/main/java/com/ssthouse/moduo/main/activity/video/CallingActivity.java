package com.ssthouse.moduo.main.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.fragment.video.CallingFragment;

/**
 * 打视频电话(等待接通)
 * Created by ssthouse on 2016/2/1.
 */
public class CallingActivity extends AppCompatActivity {

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
        initView();
        initFragment();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("通话视频");
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
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

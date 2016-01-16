package com.ssthouse.moduo.main.view.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.view.fragment.NewGestureFragment;

/**
 * 手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class GestureLockActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;

    private NewGestureFragment newGestureFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, GestureLockActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_lock);

        initView();

        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        newGestureFragment = new NewGestureFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("新建图形密码");
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

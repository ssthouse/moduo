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
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.view.fragment.gesture.EditGestureFragment;
import com.ssthouse.moduo.main.view.fragment.gesture.NewGestureFragment;

/**
 * 手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class GestureLockActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private NewGestureFragment newGestureFragment;
    private EditGestureFragment editGestureFragment;

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

        //初始fragment切换
        if (SettingManager.getInstance(this).getGestureLock().length() > 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.id_fragment_container, editGestureFragment)
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .replace(R.id.id_fragment_container, newGestureFragment)
                    .commit();
        }
    }

    public void toNewGestureFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, newGestureFragment)
                .commit();
    }

    public void toEditGestureFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, editGestureFragment)
                .commit();
    }

    private void initFragment() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        newGestureFragment = new NewGestureFragment();
        editGestureFragment = new EditGestureFragment();

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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

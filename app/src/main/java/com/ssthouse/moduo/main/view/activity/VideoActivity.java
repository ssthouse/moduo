package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.view.fragment.video.CallingFragment;
import com.ssthouse.moduo.main.view.fragment.video.VideoFragment;

import timber.log.Timber;

/**
 * 视频对话activity
 * Created by ssthouse on 2015/12/17.
 */
public class VideoActivity extends AppCompatActivity{

    private FragmentManager fragmentManager;
    private CallingFragment callingFragment;
    private VideoFragment videoFragment;

    //等待dialog
    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context 上下文
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, VideoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video);
        initView();
        initFragment();
    }

    private void initFragment() {
        //初始化fragment
        fragmentManager = getSupportFragmentManager();
        callingFragment = new CallingFragment();
        videoFragment = new VideoFragment();
        //改变界面
        showCallingFragment();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("视频控制");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();

        //开始calling
        Timber.e("开始打电话.....");
    }

    public void showCallingFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, callingFragment)
                .commit();
    }

    public void showVideoFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, videoFragment)
                .commit();
    }

    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

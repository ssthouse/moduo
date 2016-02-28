package com.ssthouse.moduo.activity.video;

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
import com.ssthouse.moduo.fragment.video.VideoFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频对话activity
 * Created by ssthouse on 2015/12/17.
 */
public class VideoActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    //fragment管理
    private static final String TAG_VIDEO_FRAGMENT = "videoFragment";
    private FragmentManager fragmentManager;
    private VideoFragment videoFragment;

    //是否竖屏
    public static boolean isPortrait = true;

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
        if (isPortrait) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        initFragment();
        initView();
    }

    private void initFragment() {
        //初始化fragment
        fragmentManager = getSupportFragmentManager();
        videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(TAG_VIDEO_FRAGMENT);
        if (videoFragment == null) {
            videoFragment = new VideoFragment();
            //改变界面
            fragmentManager.beginTransaction()
                    .replace(R.id.id_fragment_container, videoFragment, TAG_VIDEO_FRAGMENT)
                    .commit();
        }
    }

    //设置标题
    private void setTitle(String strTitle) {
        TextView tv = (TextView) toolbar.findViewById(R.id.id_tb_title);
        tv.setText(strTitle);
    }

    private void initView() {
        setSupportActionBar(toolbar);
        setTitle("视频控制");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isPortrait) {
            videoFragment.showCtrlPanel();
        } else {
            videoFragment.hideCtrlPanel();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isPortrait) {
                    finish();
                } else {
                    //变回竖屏
                    videoFragment.toPortrait();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

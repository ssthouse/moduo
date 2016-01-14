package com.ssthouse.moduo.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.view.fragment.CallingFragment;
import com.ssthouse.moduo.main.view.fragment.VideoFragment;

import timber.log.Timber;

/**
 * 视频对话activity
 * Created by ssthouse on 2015/12/17.
 */
public class VideoActivity extends AppCompatActivity{
    /**
     * 传给fragment的argument的key
     */
    public static final String ARGUMENT_CID_NUMBER = "cid";

    public static final String EXTRA_CID_NUMBER = "cid";

    private FragmentManager fragmentManager;
    private CallingFragment callingFragment;
    private VideoFragment videoFragment;

    //等待dialog
    private MaterialDialog waitDialog;

    /**
     * 启动当前activity
     *
     * @param context 上下文
     * @param cid     采集端cid
     */
    public static void start(Context context, long cid) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("cid", cid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏---不息屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video);

        //初始化fragment
        fragmentManager = getSupportFragmentManager();
        callingFragment = new CallingFragment();
        videoFragment = VideoFragment.newInstance(getIntent().getLongExtra(EXTRA_CID_NUMBER, 0));

        initView();
    }

    private void initView() {
        //等待dialog
        waitDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .customView(R.layout.dialog_wait, true)
                .build();

        //开始calling
        Timber.e("开始打电话.....");
        //改变界面
        showCallingFragment();
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

    public void dismissDialog() {
        waitDialog.dismiss();
    }
}

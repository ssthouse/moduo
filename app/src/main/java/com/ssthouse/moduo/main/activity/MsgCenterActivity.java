package com.ssthouse.moduo.main.activity;

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
import com.ssthouse.moduo.main.model.bean.event.view.MsgActivityToDetailEvent;
import com.ssthouse.moduo.main.model.bean.event.view.MsgActivityToListEvent;
import com.ssthouse.moduo.main.view.fragment.message.MsgDetailFragment;
import com.ssthouse.moduo.main.view.fragment.message.MsgListFragment;

import de.greenrobot.event.EventBus;

/**
 * 消息中心Activity
 * Created by ssthouse on 2016/1/21.
 */
public class MsgCenterActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private MsgListFragment msgListFragment;
    private MsgDetailFragment msgDetailFragment;

    public static void start(Context context){
        Intent intent = new Intent(context, MsgCenterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        EventBus.getDefault().register(this);
        initView();
        initFragment();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("消息中心");
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        msgListFragment = new MsgListFragment();
        msgDetailFragment = new MsgDetailFragment();
        //初始进入msgList
        fragmentManager.beginTransaction()
                .add(R.id.id_fragment_container, msgListFragment)
                .add(R.id.id_fragment_container, msgDetailFragment)
                .hide(msgDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    /**
     * 返回列表回调
     *
     * @param event
     */
    public void onEventMainThread(MsgActivityToListEvent event) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        fragmentManager.beginTransaction()
                .show(msgListFragment)
                .hide(msgDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    /**
     * 跳转消息详情fragment
     *
     * @param event
     */
    public void onEventMainThread(MsgActivityToDetailEvent event) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager.beginTransaction()
                .hide(msgListFragment)
                .show(msgDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EventBus.getDefault().post(new MsgActivityToListEvent());
        }
        return super.onOptionsItemSelected(item);
    }
}

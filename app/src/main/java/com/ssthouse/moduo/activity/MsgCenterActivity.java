package com.ssthouse.moduo.activity;

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
import com.ssthouse.moduo.fragment.message.MsgDetailFragment;
import com.ssthouse.moduo.fragment.message.MsgListFragment;
import com.ssthouse.moduo.model.event.view.MsgActivityToDetailEvent;
import com.ssthouse.moduo.model.event.view.MsgActivityToListEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 消息中心Activity
 * 接受事件{
 *     MsgActivityToListEvent: 切换到消息列表Fragment的event
 *     MsgActivityToDetailEvent: 切换到消息详情Fragment的event
 * }
 * Created by ssthouse on 2016/1/21.
 */
public class MsgCenterActivity extends AppCompatActivity {

    @Bind(R.id.id_tb)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private MsgListFragment msgListFragment;
    private MsgDetailFragment msgDetailFragment;

    //Fragment状态
    private State currentState = State.STATE_MSG_LIST;

    enum State {
        STATE_MSG_LIST, STATE_MSG_DETAIL
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MsgCenterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("消息中心");
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
        //改变状态
        currentState = State.STATE_MSG_LIST;
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
        //改变状态
        currentState = State.STATE_MSG_DETAIL;
        fragmentManager.beginTransaction()
                .hide(msgListFragment)
                .show(msgDetailFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        //todo---填充MsgDetailFragment数据
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (currentState == State.STATE_MSG_LIST) {
                finish();
            } else if (currentState == State.STATE_MSG_DETAIL) {
                EventBus.getDefault().post(new MsgActivityToListEvent());
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
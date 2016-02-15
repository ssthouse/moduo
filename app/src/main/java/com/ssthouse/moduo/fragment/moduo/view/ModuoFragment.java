package com.ssthouse.moduo.fragment.moduo.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.moduo.control.util.DimenUtil;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MainAdapter;
import com.ssthouse.moduo.fragment.moduo.view.adapter.MsgBean;
import com.ssthouse.moduo.fragment.moduo.presenter.ModuoPresenter;
import com.ssthouse.moduo.fragment.moduo.view.widget.ModuoView;
import com.ssthouse.moduo.fragment.moduo.view.widget.record.RecordButton;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

/**
 * 魔哆主界面
 * Created by ssthouse on 2016/1/24.
 */
public class ModuoFragment extends Fragment implements ModuoFragmentView{

    private ModuoPresenter mModuoPresenter;

    //魔哆图案
    @Bind(R.id.id_moduo)
    ModuoView moduoView;

    //录音按钮
    @Bind(R.id.id_btn_record)
    RecordButton btnRecord;

    //聊天列表
    @Bind(R.id.id_swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @Bind(R.id.id_recycle_chat)
    RecyclerView recycleChat;
    MainAdapter mAdapter;

    private int bigModuoHeight;
    private int smallModuoHeight;
    private static final int ANIMATE_TWEEN = 500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_moduo, container, false);
        ButterKnife.bind(this, rootView);
        initView();

        //presenter
        mModuoPresenter = new ModuoPresenter(this);
        return rootView;
    }

    private void initDimens() {
        bigModuoHeight = moduoView.getHeight();
        smallModuoHeight = DimenUtil.dp2px(getContext(), 100);
    }

    private void initView() {
        //聊天列表
        recycleChat.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MainAdapter(getContext(), recycleChat);
        recycleChat.setAdapter(mAdapter);
        recycleChat.setItemAnimator(new LandingAnimator(new AccelerateDecelerateInterpolator()));

        //刷洗响应
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mModuoPresenter.loadMoreMsg();
            }
        });

        moduoView.post(new Runnable() {
            @Override
            public void run() {
                initDimens();
            }
        });
    }


    @Override
    public void animate2Big() {
        if (moduoView.getCurrentState() == ModuoView.State.STATE_BIG) {
            return;
        }
        ViewAnimator.animate(swipeLayout)
                .height(bigModuoHeight - smallModuoHeight, 0)
                .andAnimate(moduoView)
                .height(smallModuoHeight, bigModuoHeight)
                .interpolator(new AccelerateDecelerateInterpolator())
                .duration(ANIMATE_TWEEN)
                .start();
    }

    @Override
    public void animate2Small() {
        if (moduoView.getCurrentState() == ModuoView.State.STATE_SMALL) {
            return;
        }
        swipeLayout.setVisibility(View.VISIBLE);
        ViewAnimator.animate(swipeLayout)
                .height(0, bigModuoHeight - smallModuoHeight)
                .andAnimate(moduoView)
                .height(bigModuoHeight, smallModuoHeight)
                .interpolator(new AccelerateDecelerateInterpolator())
                .duration(ANIMATE_TWEEN)
                .start();
    }

    @Override
    public void loadMoreMsg(List<MsgBean> msgList) {
        if (msgList == null || msgList.size() == 0) {
            Toast.makeText(getContext(), "没有更多记录了", Toast.LENGTH_SHORT).show();
        } else {
            mAdapter.addMgList2Top(msgList);
        }
        //清除刷新状态
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void addMsgBean(MsgBean msgBean) {
        mAdapter.addMsg(msgBean);
    }

    @Override
    public MsgBean getTopMsgBean() {
        return mAdapter.getMsgList().get(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mModuoPresenter.destroy();
    }
}

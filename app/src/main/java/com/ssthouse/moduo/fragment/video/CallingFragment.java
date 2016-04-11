package com.ssthouse.moduo.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.video.VideoActivity;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.video.Communication;
import com.ssthouse.moduo.model.event.video.CallingResponseEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 拨号Fragment
 * 电话接通条件: 接收到设备推送的video变化为1---表示对方接受
 * 接受事件{
 *     CallingResponseEvent: 拨号结果事件回调
 * }
 * Created by ssthouse on 2016/1/12.
 */
public class CallingFragment extends Fragment {

    @Bind(R.id.id_iv_avatar)
    ImageView ivAvatar;

    @Bind(R.id.id_btn_cancel)
    ImageView btnCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.fragment_calling, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        //todo---模拟接通电话
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    EventBus.getDefault().post(new CallingResponseEvent(true));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new CallingResponseEvent(true));
            }
        });
        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出Activity
                getActivity().finish();
            }
        });
    }

    /**
     * 电话接听回调
     *
     * @param event
     */
    public void onEventMainThread(CallingResponseEvent event) {
        Timber.e("收到电话接通结果回调");
        if (!Communication.getInstance(getContext()).isLogin()) {
            Toast.show("视频服务未连接");
            return;
        }
        if (event.isSuccess()) {
            //关闭callingActivity
            getActivity().finish();
            //启动videoActivity
            VideoActivity.start(getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package com.ssthouse.moduo.fragment.sliding.main.View;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.ModuoActivity;
import com.ssthouse.moduo.activity.MsgCenterActivity;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.fragment.sliding.IFragmentUI;
import com.ssthouse.moduo.fragment.sliding.main.presenter.MainFragmentPresenter;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 主界面
 * Created by ssthouse on 2016/1/13.
 */
public class MainFragment extends Fragment implements MainFragmentView, IFragmentUI {

    //Presenter
    private MainFragmentPresenter mMainFragmentPresenter;

    @Bind(R.id.id_iv_home_control)
    ImageView ivHomeControl;
    @Bind(R.id.id_iv_video)
    ImageView ivVideo;
    @Bind(R.id.id_iv_message)
    ImageView ivMessage;

    //魔哆状态[ 登陆失败  登陆成功(未连接魔哆) 魔哆连接成功]
    @Bind(R.id.id_tv_moduo_state)
    TextView tvModuoState;
    private MaterialDialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        initView();

        //Presenter
        mMainFragmentPresenter = new MainFragmentPresenter(getContext(), this);

        //后台登陆机智云
        Observable.just("")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        mMainFragmentPresenter.tryLogin();
                        return null;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Timber.e("try log int completed");
                    }
                });

        return rootView;
    }

    private void initView() {
        ivHomeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (XPGController.getCurrentDevice() == null) {
                    ToastHelper.show(getContext(), "当前没有设备连接");
                } else {
                    ModuoActivity.start(getContext());
//                    HomeControlActivity.start(getContext());
//                    XPGController.getCurrentDevice().getXpgWifiDevice().login(
//                            SettingManager.getInstance(getContext()).getUid(),
//                            SettingManager.getInstance(getContext()).getToken()
//                    );
                }
            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (XPGController.getCurrentDevice() == null) {
                    ToastHelper.show(getContext(), "当前没有设备连接");
                    return;
                }
                //跳转视频Activity
                mMainFragmentPresenter.jump2Video();
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgCenterActivity.start(getContext());
            }
        });

        waitDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .autoDismiss(false)
                .build();
    }

    @Override
    public void showDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissDialog() {
        waitDialog.dismiss();
    }

    @Override
    public void updateUI() {
        //是否账号登陆
        if (!XPGController.isLogin()) {
            tvModuoState.setText("未登录");
            return;
        } else {
            tvModuoState.setText("用户登录成功, 未连接魔哆");
        }
        if (XPGController.getCurrentDevice() != null) {
            tvModuoState.setText("魔哆连接成功,欢迎使用");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMainFragmentPresenter.destroy();
    }
}

package com.ssthouse.moduo.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.model.event.RegisterActivityDestoryEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.xpg.RegisterResultEvent;
import com.ssthouse.moduo.view.activity.LoadingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 邮箱注册fragment
 * Created by ssthouse on 2015/12/19.
 */
public class EmailRegisterFragment extends Fragment {

    /**
     * 判断当前fragment是否在被使用
     * 是否要对Eventbus发出的事件进行响应
     */
    private boolean isInUse = false;

    @Bind(R.id.id_et_email_address)
    EditText etEmailAddr;

    @Bind(R.id.id_et_password)
    EditText etPassword;

    @Bind(R.id.id_btn_start_email_register)
    Button btnStartEmailRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = View.inflate(getContext(), R.layout.fragment_email_register, null);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        btnStartEmailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---判断邮箱合法
                //确认当前fragment在使用
                isInUse = true;
                //email注册
                String email = etEmailAddr.getText().toString();
                String password = etPassword.getText().toString();
                XPGController.getInstance(getContext()).getmCenter().cRegisterMailUser(email, password);
            }
        });
    }

    /**
     * 注册结果回调
     *
     * @param event
     */
    public void onEventMainThread(RegisterResultEvent event) {
        //判断当前fragment是否响应外界的event
        if (isInUse) {
            if (event.isSuccess()) {
                ToastHelper.show(getContext(), "注册成功, 正在登陆...");
                //TODO---尝试登陆---保存uid和token和邮箱--和密码到本地
                XPGController.getInstance(getContext()).getmCenter()
                        .cLogin(etEmailAddr.getText().toString(), etPassword.getText().toString());
            } else {
                ToastHelper.show(getContext(), "注册失败");
            }
        }
    }

    /**
     * 登陆结果回调方法
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (isInUse) {
            if (event.isSuccess()) {
                //// TODO: 2015/12/21
                isInUse = false;
                ToastHelper.show(getContext(), "登陆成功");
                //保存登陆数据
                SettingManager.getInstance(getContext()).setUserName(etEmailAddr.getText().toString());
                SettingManager.getInstance(getContext()).setPassword(etPassword.getText().toString());
                SettingManager.getInstance(getContext()).setLoginInfo(event);
                PreferenceHelper.getInstance(getContext()).setIsFistIn(false);
                //跳转loading activity
                LoadingActivity.start(getContext());
                //退出当前activity
                EventBus.getDefault().post(new RegisterActivityDestoryEvent());
            } else {
                ToastHelper.show(getContext(), "登陆失败");
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

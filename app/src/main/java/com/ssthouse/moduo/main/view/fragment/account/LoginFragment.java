package com.ssthouse.moduo.main.view.fragment.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;
import com.ssthouse.moduo.main.control.util.PreferenceHelper;
import com.ssthouse.moduo.main.control.util.StringUtils;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.model.bean.event.xpg.RegisterFragmentChangeEvent;
import com.ssthouse.moduo.main.model.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.view.activity.LoadingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 登陆fragment
 * Created by ssthouse on 2015/12/19.
 */
public class LoginFragment extends Fragment {
    /**
     * 是否在使用当前fragment承接eventbus事件
     */
    boolean isInUse = false;

    /**
     * 用户名输入框
     */
    @Bind(R.id.id_et_username)
    EditText etUsername;
    /**
     * 密码输入框
     */
    @Bind(R.id.id_et_password)
    EditText etPassword;
    /**
     * 登陆按钮
     */
    @Bind(R.id.id_btn_login)
    Button btnLogin;
    /**
     * 手机号注册
     */
    @Bind(R.id.id_tv_phone_register)
    TextView tvPhoneRegister;
    /**
     * 邮箱注册
     */
    @Bind(R.id.id_tv_email_register)
    TextView tvEmailRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = View.inflate(getContext(), R.layout.fragment_login, null);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e(".....what happened?");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                //检查格式
                if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
                    ToastHelper.show(getContext(), "用户名和密码不可为空");
                    return;
                }
                //TODO---格式检查---登陆
                isInUse = true;
                XPGController.getInstance(getContext()).getmCenter()
                        .cLogin(username, password);
                Timber.e(".....something wrong?");

            }
        });

        tvPhoneRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RegisterFragmentChangeEvent(
                        RegisterFragmentChangeEvent.NextFragment.PHONE_REGISTER_FRAGMENT));
            }
        });

        tvEmailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RegisterFragmentChangeEvent(
                        RegisterFragmentChangeEvent.NextFragment.EMAIL_REGISTER_FRAGMENT));
            }
        });
    }

    /**
     * 登陆结果回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if(isInUse) {
            if (event.isSuccess()) {
                //todo
                isInUse = false;
                ToastHelper.show(getContext(), "登陆成功");
                //保存登陆数据
                PreferenceHelper.getInstance(getContext()).setIsFistIn(false);
                SettingManager.getInstance(getContext()).setUserName(etUsername.getText().toString());
                SettingManager.getInstance(getContext()).setPassword(etPassword.getText().toString());
                SettingManager.getInstance(getContext()).setLoginCacheInfo(event);
                //跳转loading activity
                LoadingActivity.start(getContext());
                //退出activity
                getActivity().finish();
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

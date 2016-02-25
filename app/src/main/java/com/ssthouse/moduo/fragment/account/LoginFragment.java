package com.ssthouse.moduo.fragment.account;

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
import com.ssthouse.moduo.activity.GuideActivity;
import com.ssthouse.moduo.control.util.MD5Util;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.StringUtils;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.xpg.RegisterFragmentChangeEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 用于第一次进入app的登陆fragment
 * Created by ssthouse on 2015/12/19.
 */
public class LoginFragment extends Fragment {
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

    @Bind(R.id.id_btn_login_anonymous)
    Button btnLoginAnonymous;

    /**暂时不用************************************/
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
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = MD5Util.getMdStr(etPassword.getText().toString());
                //检查格式
                if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                    ToastHelper.show(getContext(), "用户名和密码不可为空");
                    return;
                }
                //尝试登陆
                XPGController.getInstance(getContext()).getmCenter()
                        .cLogin(username, password);
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

        //匿名登录
        btnLoginAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ex
            }
        });
    }

    /**
     * 登陆结果回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (event.isSuccess()) {
            ToastHelper.show(getContext(), "登陆成功");
            //保存登陆数据
            PreferenceHelper.getInstance(getContext()).setIsFistIn(false);
            String username = etUsername.getText().toString();
            String password = MD5Util.getMdStr(etPassword.getText().toString());
            SettingManager settingManager = SettingManager.getInstance(getContext());
            settingManager.setUserName(username);
            settingManager.setPassword(password);
            settingManager.setLoginCacheInfo(event);
            //todo---通知GuideActivity进入下一步
            GuideActivity guideActivity = (GuideActivity) getActivity();
            guideActivity.switchFragment(GuideActivity.State.STATE_GESTURE_LOCK);
        } else {
            ToastHelper.show(getContext(), "登陆失败");
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

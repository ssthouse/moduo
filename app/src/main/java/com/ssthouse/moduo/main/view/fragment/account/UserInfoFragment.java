package com.ssthouse.moduo.main.view.fragment.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.event.account.RegisterResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.xpg.XPGController;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 用户账号
 * Created by ssthouse on 2016/1/13.
 */
public class UserInfoFragment extends Fragment {

    private Button btnLogin;

    private MaterialDialog loginOrRegisterDialog;

    private EditText etUsername;
    private EditText etPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        initView(rootView);
        Timber.e("onCreateView");
        EventBus.getDefault().register(this);
        return rootView;
    }

    private void initView(View rootView) {
        btnLogin = (Button) rootView.findViewById(R.id.id_btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOrRegisterDialog.show();
            }
        });

        loginOrRegisterDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_login_or_register, true)
                .autoDismiss(false)
                .build();
        etUsername = (EditText) loginOrRegisterDialog.getCustomView().findViewById(R.id.id_et_username);
        etPassword = (EditText) loginOrRegisterDialog.getCustomView().findViewById(R.id.id_et_password);
        //登陆
        loginOrRegisterDialog.getCustomView().findViewById(R.id.id_btn_login)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString().hashCode() + "";
                        Timber.e(username + " : " + password);
                        XPGController.getInstance(getContext()).getmCenter()
                                .cLogin(username, password);
                    }
                });
        //注册
        loginOrRegisterDialog.getCustomView().findViewById(R.id.id_btn_register)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString().hashCode() + "";
                        Timber.e(username + " : " + password);
                        XPGController.getInstance(getContext()).getmCenter()
                                .cRegisterUser(username, password);
                    }
                });
    }

    /**
     * 注册回调
     *
     * @param event
     */
    public void onEventMainThread(RegisterResultEvent event) {
        if(!ActivityUtil.isTopActivity(getActivity(), "MainActivity")){
            return;
        }
        if(event.isSuccess()) {
            Timber.e("注册成功");
        }else{
            Timber.e("登陆失败");
        }
    }

    /**
     * 登陆回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if(!ActivityUtil.isTopActivity(getActivity(), "MainActivity")){
            return;
        }
        if(event.isSuccess()) {
            Timber.e("登陆成功");
        }else{
            Timber.e("登陆失败");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

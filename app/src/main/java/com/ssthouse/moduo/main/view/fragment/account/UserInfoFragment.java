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

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.bean.cons.xpg.GizwitsErrorMsg;
import com.ssthouse.moduo.bean.event.account.AnonymousUserTransEvent;
import com.ssthouse.moduo.bean.event.account.RegisterResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.util.MD5Util;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 用户账号
 * Created by ssthouse on 2016/1/13.
 */
public class UserInfoFragment extends Fragment {

    private Button btnLogin;
    private Button btnLogOut;

    private MaterialDialog loginOrRegisterDialog;
    private MaterialDialog waitDialog;

    private EditText etUsername;
    private EditText etPassword;

    private TextView tvUsername;
    private TextView tvPassword;

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
        tvUsername = (TextView) rootView.findViewById(R.id.id_tv_user_name);
        tvPassword = (TextView) rootView.findViewById(R.id.id_tv_password);

        btnLogin = (Button) rootView.findViewById(R.id.id_btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOrRegisterDialog.show();
            }
        });

        btnLogOut = (Button) rootView.findViewById(R.id.id_btn_log_out);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/1/20 注销账户--mainFragment也要改变
                SettingManager.getInstance(getContext()).clean();
                XPGController.setCurrentDevice(null);
            }
        });

        waitDialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .autoDismiss(false)
                .build();

        loginOrRegisterDialog = new MaterialDialog.Builder(getContext())
                .title("登陆")
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
                        if (etUsername.getText().toString().length() < 6
                                || etPassword.getText().toString().length() < 6) {
                            ToastHelper.show(getContext(), "用户名或密码不可少于6位");
                            return;
                        }
                        waitDialog.show();
                        String username = etUsername.getText().toString();
                        String password = MD5Util.getMdStr(etPassword.getText().toString());
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
                        if (etUsername.getText().toString().length() < 6
                                || etPassword.getText().toString().length() < 6) {
                            ToastHelper.show(getContext(), "用户名或密码不可少于6位");
                            return;
                        }
                        waitDialog.show();
                        String username = etUsername.getText().toString();
                        String password = MD5Util.getMdStr(etPassword.getText().toString());
                        Timber.e(username + " : " + password);
                        //判断是否为匿名登录用户
                        if (SettingManager.getInstance(getContext()).isAnonymousUser()) {
                            //匿名转普通用户
                            XPGController.getInstance(getContext()).getmCenter()
                                    .cTransferToNormalUser(SettingManager.getInstance(getContext()).getToken(),
                                            username, password);
                        } else {
                            XPGController.getInstance(getContext())
                                    .getmCenter()
                                    .cRegisterUser(username, password);
                        }
                    }
                });

        //根据当前登陆状况---显示下方的操作按钮
        if (SettingManager.getInstance(getContext()).isLogined()) {
            //是否为匿名登录
            if (SettingManager.getInstance(getContext()).isAnonymousUser()) {
                btnLogin.setVisibility(View.VISIBLE);
                btnLogin.setText("当前为匿名登录");
                tvUsername.setText("匿名");
                tvPassword.setText("匿名");
            } else {
                tvUsername.setText(SettingManager.getInstance(getContext()).getUserName());
                tvPassword.setText(SettingManager.getInstance(getContext()).getPassword());
                btnLogOut.setVisibility(View.VISIBLE);
            }
        } else {
            tvUsername.setText("未登陆");
            tvPassword.setText("未登录");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setText("登陆");
        }
    }

    /**
     * 注册回调
     *
     * @param event
     */
    public void onEventMainThread(RegisterResultEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity")) {
            return;
        }
        if (event.isSuccess()) {
            Timber.e("注册成功");
            //登陆
            XPGController.getInstance(getContext()).getmCenter().cLogin(etUsername.getText().toString(),
                    MD5Util.getMdStr(etPassword.getText().toString()));
        } else {
            Timber.e("登陆失败");
            ToastHelper.show(getContext(), GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
    }

    /**
     * 登陆回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity")) {
            return;
        }
        loginOrRegisterDialog.dismiss();
        waitDialog.dismiss();
        if (event.isSuccess()) {
            Timber.e("登陆成功");
        } else {
            Timber.e("登陆失败");
        }
    }

    /**
     * 匿名用户转普通用户回调
     *
     * @param event
     */
    public void onEventMainThread(AnonymousUserTransEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity")) {
            return;
        }
        waitDialog.dismiss();
        if (event.isSuccess()) {
            Timber.e("匿名用户转换成功");
            //todo---登陆
            waitDialog.show();
            //登陆
            XPGController.getInstance(getContext()).getmCenter().cLogin(etUsername.getText().toString(),
                    MD5Util.getMdStr(etPassword.getText().toString()));
        } else {
            Timber.e("匿名用户转换失败");
            ToastHelper.show(getContext(), GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

package com.ssthouse.moduo.main.view.fragment.main;

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
import com.ssthouse.moduo.bean.UserInfo;
import com.ssthouse.moduo.bean.cons.xpg.GizwitsErrorMsg;
import com.ssthouse.moduo.bean.event.account.AnonymousUserTransEvent;
import com.ssthouse.moduo.bean.event.account.RegisterResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.bean.event.xpg.XPGLogoutEvent;
import com.ssthouse.moduo.main.control.util.ActivityUtil;
import com.ssthouse.moduo.main.control.util.CloudUtil;
import com.ssthouse.moduo.main.control.util.MD5Util;
import com.ssthouse.moduo.main.control.util.NetUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;
import com.ssthouse.moduo.main.control.xpg.XPGController;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 用户账号
 * Created by ssthouse on 2016/1/13.
 */
public class UserInfoFragment extends Fragment implements IFragmentUI {

    private View rootView;

    private Button btnLogin;
    private Button btnLogOut;

    private MaterialDialog loginOrRegisterDialog;
    private MaterialDialog waitDialog;

    private EditText etUsername;
    private EditText etPassword;

    private TextView tvUsername;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        initView(rootView);
        updateUI();
        return rootView;
    }

    private void initView(View rootView) {
        tvUsername = (TextView) rootView.findViewById(R.id.id_tv_user_name);

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
                showWaitDialog("正在注销账户");
                //// TODO: 2016/1/20 注销账户--mainFragment也要改变
                XPGController.getInstance(getContext()).getmCenter().cLogout();
                SettingManager.getInstance(getContext()).clean();
                XPGController.setCurrentDevice(null);
                //刷新界面
                getView();
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
                        if (!NetUtil.isConnected(getContext())) {
                            ToastHelper.showNoInternet(getContext());
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
                        if (!NetUtil.isConnected(getContext())) {
                            ToastHelper.showNoInternet(getContext());
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
    }

    /**
     * 刷新UI
     */
    @Override
    public void updateUI() {
        //根据当前登陆状况---显示下方的操作按钮
        if (SettingManager.getInstance(getContext()).isLogined()) {
            //是否为匿名登录
            if (SettingManager.getInstance(getContext()).isAnonymousUser()) {
                btnLogin.setVisibility(View.VISIBLE);
                btnLogin.setText("实名登陆");
                btnLogOut.setVisibility(View.GONE);
                tvUsername.setText("匿名");
            } else {
                btnLogin.setVisibility(View.GONE);
                tvUsername.setText(SettingManager.getInstance(getContext()).getUserName());
                btnLogOut.setVisibility(View.VISIBLE);
            }
        } else {
            tvUsername.setText("未登陆");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setText("登陆");
            btnLogOut.setVisibility(View.GONE);
        }
    }

    /**
     * 显示等待dialog
     *
     * @param msg
     */
    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialog.getCustomView().findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
    }

    /**
     * 保存当前用户名密码
     */
    private void saveCurrentUserInfo() {
        if (etUsername.getText().toString().length() < 6
                || etPassword.getText().toString().length() < 6) {
            return;
        }
        //用户数据保存带本地
        SettingManager.getInstance(getContext()).setUserName(etUsername.getText().toString());
        SettingManager.getInstance(getContext()).setPassword(MD5Util.getMdStr(etPassword.getText().toString()));
        //用户数据保存到云端
        CloudUtil.saveUserInfoToCloud(new UserInfo(etUsername.getText().toString(),
                        MD5Util.getMdStr(etPassword.getText().toString()),
                        SettingManager.getInstance(getContext()).getGestureLock()),
                null);
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
            //注册成功保存账号
            saveCurrentUserInfo();
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
            CloudUtil.updateUserInfo(getContext(), SettingManager.getInstance(getContext()).getUserName());
            //保存机智云登陆数据
            SettingManager.getInstance(getContext()).setLoginCacheInfo(event);
            //登陆成功保存当前账号
            saveCurrentUserInfo();
            //刷新界面
            updateUI();
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
            //登陆等待dialog
            waitDialog.show();
            //保存当前用户信息
            saveCurrentUserInfo();
            //登陆
            XPGController.getInstance(getContext()).getmCenter().cLogin(etUsername.getText().toString(),
                    MD5Util.getMdStr(etPassword.getText().toString()));
        } else {
            Timber.e("匿名用户转换失败");
            ToastHelper.show(getContext(), GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
    }

    public void onEventMainThread(XPGLogoutEvent event) {
        waitDialog.dismiss();
        ToastHelper.show(getContext(), "注销成功");
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

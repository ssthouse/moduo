package com.ssthouse.moduo.fragment.sliding;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.MD5Util;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.cons.xpg.GizwitsErrorMsg;
import com.ssthouse.moduo.model.event.account.AnonymousUserTransEvent;
import com.ssthouse.moduo.model.event.account.RegisterResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLogoutEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 用户账号控制fragment
 * 接受事件{
 *     RegisterResultEvent: 注册结果回调事件
 *     XPGLoginResultEvent: 机智云登录回调事件
 *     AnonymousUserTransEvent: 匿名用户转实名用户回调
 *     XpgLogoutEvent: 机智云登出事件
 * }
 * Created by ssthouse on 2016/1/13.
 */
public class UserInfoFragment extends Fragment implements IFragmentUI {

    //实名登陆
    @Bind(R.id.id_btn_login)
    Button btnLogin;
    //登出
    @Bind(R.id.id_btn_log_out)
    Button btnLogOut;
    //用户名
    @Bind(R.id.id_tv_username)
    TextView tvUsername;
    //密码
    @Bind(R.id.id_tv_password)
    TextView tvPassword;

    //登陆  注册  Dialog
    private Dialog loginOrRegisterDialog;
    private View loginOrRegisterDialogView;

    //等待dialog
    private Dialog waitDialog;
    private View waitDialogView;

    //确认dialog
    private Dialog confirmDialog;
    private View confirmDialogView;

    private EditText etUsername;
    private EditText etPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        initDialog();
        updateUI();
        return rootView;
    }

    private void initView() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginOrRegisterDialog.show();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog("确认注销?");
                //刷新界面
                getView();
            }
        });
    }

    private void initDialog() {
        //等待dialog
        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);

        //确认注销dialog
        confirmDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_msg_confirm, null);
        confirmDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .create();

        //登陆或注册dialog
        loginOrRegisterDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login_or_register, null);
        loginOrRegisterDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(loginOrRegisterDialogView)
                .create();
        etUsername = (EditText) loginOrRegisterDialogView.findViewById(R.id.id_et_username);
        etPassword = (EditText) loginOrRegisterDialogView.findViewById(R.id.id_et_password);
        //登陆
        loginOrRegisterDialogView.findViewById(R.id.id_btn_login)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etUsername.getText().toString().length() < 6
                                || etPassword.getText().toString().length() < 6) {
                            Toast.show("用户名或密码不可少于6位");
                            return;
                        }
                        if (!NetUtil.isConnected(getContext())) {
                            Toast.showNoInternet();
                        }
                        showWaitDialog("正在登陆");
                        String username = etUsername.getText().toString();
                        String password = MD5Util.getMdStr(etPassword.getText().toString());
                        Timber.e(username + " : " + password);
                        XPGController.getInstance(getContext()).getmCenter()
                                .cLogin(username, password);
                    }
                });
        //注册
        loginOrRegisterDialogView.findViewById(R.id.id_btn_register)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (etUsername.getText().toString().length() < 6
                                || etPassword.getText().toString().length() < 6) {
                            Toast.show("用户名或密码不可少于6位");
                            return;
                        }
                        if (!NetUtil.isConnected(getContext())) {
                            Toast.showNoInternet();
                            return;
                        }
                        showWaitDialog("正在注册");
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
        //刷新文字数据
        getView();

        //根据当前登陆状况---显示下方的操作按钮
        SettingManager settingManager = SettingManager.getInstance(getContext());
        //未登录
        if (!settingManager.isLogined()) {
            tvUsername.setText("未登录");
            tvPassword.setText("未登录");
            btnLogin.setEnabled(true);
            btnLogOut.setEnabled(false);
            return;
        }
        //匿名登录
        if (settingManager.isAnonymousUser()) {
            tvUsername.setText("匿名登录");
            tvPassword.setText("匿名登录");
            btnLogin.setEnabled(true);
            btnLogOut.setEnabled(false);
            return;
        }
        //实名登陆---无法获取password长度
        tvUsername.setText(settingManager.getUserName());
        String strPassword = "";
        for (int i = 0; i < settingManager.getUserName().length(); i++) {
            strPassword += "*";
        }
        tvPassword.setText(strPassword);
        btnLogin.setEnabled(false);
        btnLogOut.setEnabled(true);
    }

    /**
     * 显示等待dialog
     *
     * @param msg
     */
    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    private void showConfirmDialog(String msg) {
        TextView tvConfirm = (TextView) confirmDialogView.findViewById(R.id.id_tv_content);
        tvConfirm.setText(msg);
        confirmDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();    //隐藏当前confirm dialog
                showWaitDialog("正在注销"); //显示等待dialog
                //登出
                XPGController.getInstance(getContext()).getmCenter().cLogout();
            }
        });
        confirmDialogView.findViewById(R.id.id_iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.show();
    }

    /**
     * 保存当前用户名密码
     */
    private void saveInputUserInfo() {
        Timber.e("保存用户数据到本地");
        if (etUsername.getText().toString().length() < 6
                || etPassword.getText().toString().length() < 6) {
            Timber.e("用户名 | 密码  不合规范");
            return;
        }
        //用户数据保存带本地
        SettingManager.getInstance(getContext()).setUserName(etUsername.getText().toString());
        SettingManager.getInstance(getContext()).setPassword(MD5Util.getMdStr(etPassword.getText().toString()));
    }

    /**
     * 注册回调
     *
     * @param event
     */
    public void onEventMainThread(RegisterResultEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity") || isHidden()) {
            return;
        }
        if (event.isSuccess()) {
            Toast.show("注册成功");
            Timber.e("注册成功");
            //注册成功保存账号
            saveInputUserInfo();
            //登陆
            SettingManager settingManager = SettingManager.getInstance(getContext());
            XPGController.getInstance(getContext())
                    .getmCenter()
                    .cLogin(settingManager.getUserName(),
                            settingManager.getPassword());
        } else {
            Timber.e("注册失败");
            Toast.show("注册失败" + GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
        //刷新UI
        updateUI();
    }

    /**
     * 登陆回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity") || isHidden()) {
            Timber.e("not in focus");
            return;
        }
        loginOrRegisterDialog.dismiss();
        waitDialog.dismiss();
        if (event.isSuccess()) {
            Timber.e("登陆成功");
            Toast.show("登陆成功");
            //填写的用户信息保存到本地---
            saveInputUserInfo();
            //保存机智云登陆缓存数据
            SettingManager.getInstance(getContext()).setLoginCacheInfo(event);
        } else {
            Timber.e("登陆失败");
            Toast.show("登陆失败");
        }
        //刷新界面
        updateUI();
    }

    /**
     * 匿名用户转普通用户回调
     *
     * @param event
     */
    public void onEventMainThread(AnonymousUserTransEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity") || isHidden()) {
            return;
        }
        waitDialog.dismiss();
        if (event.isSuccess()) {
            Timber.e("匿名用户转换成功");
            //登陆等待dialog
            showWaitDialog("正在登陆");
            //保存当前用户信息
            saveInputUserInfo();
            //登陆
            SettingManager settingManager = SettingManager.getInstance(getContext());
            XPGController.getInstance(getContext())
                    .getmCenter()
                    .cLogin(settingManager.getUserName(), settingManager.getPassword());
        } else {
            Timber.e("匿名用户转换失败");
            Toast.show(GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
    }

    //注销回调
    public void onEventMainThread(XPGLogoutEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "MainActivity")) {
            return;
        }
        waitDialog.dismiss();
        //清空本地用户数据
        SettingManager.getInstance(getContext()).cleanUserInfo();
        XPGController.setCurrentDevice(null);
        Toast.show("注销成功");
        updateUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

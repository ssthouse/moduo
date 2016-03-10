package com.ssthouse.moduo.fragment.account;

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
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.control.util.StringUtils;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.cons.xpg.GizwitsErrorMsg;
import com.ssthouse.moduo.model.event.account.RegisterResultEvent;
import com.ssthouse.moduo.model.event.view.GuideFinishEvent;
import com.ssthouse.moduo.model.event.xpg.RegisterFragmentChangeEvent;
import com.ssthouse.moduo.model.event.xpg.XPGLoginResultEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

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

    //匿名登录
    @Bind(R.id.id_btn_login_anonymous)
    Button btnLoginAnonymous;

    //注册
    @Bind(R.id.id_btn_register)
    Button btnREgister;

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

    //等待Dialog
    private View waitDialogView;
    private Dialog waitDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        initDialog();
        return rootView;
    }

    private void initView() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查格式
                if (StringUtils.isEmpty(etUsername.getText().toString())
                        || StringUtils.isEmpty(etPassword.getText().toString())) {
                    ToastHelper.show(getContext(), "用户名和密码不可为空");
                    return;
                }
                if (!NetUtil.isConnected(getContext())) {
                    ToastHelper.show(getContext(), "当前网络不可用");
                    return;
                }
                String username = etUsername.getText().toString();
                String password = MD5Util.getMdStr(etPassword.getText().toString());
                //弹出等待登录dialog
                showWaitDialog("正在登陆请稍候");
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

        //注册
        btnREgister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查格式
                if (StringUtils.isEmpty(etUsername.getText().toString())
                        || StringUtils.isEmpty(etPassword.getText().toString())) {
                    ToastHelper.show(getContext(), "用户名和密码不可为空");
                    return;
                }
            }
        });

        //匿名登录
        btnLoginAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //匿名登录---直接跳转主页
                EventBus.getDefault().post(new GuideFinishEvent(true));
            }
        });
    }

    private void initDialog() {
        //等待登录Dialog
        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .setCancelable(false)
                .create();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
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
            Timber.e("注册成功");
            //清除本地魔哆数据
            SettingManager.getInstance(getContext()).cleanLocalModuo();
            //注册成功保存账号
            saveInputUserInfo();
            //登陆
            XPGController.getInstance(getContext()).getmCenter().cLogin(etUsername.getText().toString(),
                    MD5Util.getMdStr(etPassword.getText().toString()));
        } else {
            Timber.e("注册失败");
            ToastHelper.show(getContext(), GizwitsErrorMsg.getEqual(event.getErrorCode()).getCHNDescript());
        }
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
//        //todo---用户数据保存到云端
//        CloudUtil.updateUserInfoToCloud(new UserInfo(etUsername.getText().toString(),
//                MD5Util.getMdStr(etPassword.getText().toString()),
//                SettingManager.getInstance(getContext()).getGestureLock()));
    }

    /**
     * 登陆结果回调
     *
     * @param event
     */
    public void onEventMainThread(XPGLoginResultEvent event) {
        //隐藏dialog
        waitDialog.dismiss();
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
//            退出导航activity---继续LoadingActivity
            getActivity().finish();
            EventBus.getDefault().post(new GuideFinishEvent(true));
//            GuideActivity guideActivity = (GuideActivity) getActivity();
//            guideActivity.switchFragment(GuideActivity.State.STATE_GESTURE_LOCK);
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

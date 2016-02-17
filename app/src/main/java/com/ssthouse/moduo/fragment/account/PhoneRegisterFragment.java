package com.ssthouse.moduo.fragment.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.account.RegisterResultEvent;
import com.ssthouse.moduo.model.event.xpg.AuthCodeSendResultEvent;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 手机号注册activity:
 * <p/>
 * 处理验证码发送成功回调
 * Created by ssthouse on 2015/12/19.
 */
public class PhoneRegisterFragment extends Fragment {

    /**
     * 判断当前fragment是否在被使用
     * 是否要对Eventbus发出的事件进行响应
     */
    private boolean isInUse = false;

    /**
     * 手机号输入框
     */
    @Bind(R.id.id_et_phone_number)
    EditText etPhoneNumber;
    /**
     * 获取验证码按钮
     */
    @Bind(R.id.id_btn_get_auth)
    Button btnGetAuth;
    /**
     * 验证码输入框
     */
    @Bind(R.id.id_et_auth)
    EditText etAuth;
    /**
     * 密码输入框
     */
    @Bind(R.id.id_et_password)
    EditText etPassword;
    /**
     * 确认注册按钮
     */
    @Bind(R.id.id_btn_start_phone_register)
    Button btnStartPhoneRegister;

    /**
     * 等待Dialog
     */
    private MaterialDialog dialog;

    /**
     * 用于更新显示发送验证码按钮文字
     */
    private int secondLeft = 60;
    private static int MSG_UPDATE_BUTTON_TEXT = 1000;
    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_BUTTON_TEXT) {
                if (secondLeft <= 0) {
                    timer.cancel();
                    btnGetAuth.setEnabled(true);
                    btnGetAuth.setText("重新获取验证码");
                } else {
                    secondLeft -= 1;
                    btnGetAuth.setText(secondLeft + "秒后\n重新获取");
                }
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = View.inflate(getContext(), R.layout.fragment_phone_register, null);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        //获取验证码按钮
        btnGetAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPhoneNumber.getText().toString().trim().length() != 11) {
                    ToastHelper.show(getContext(), "手机号必须为11位");
                    return;
                }
                //TODO---发送验证码
                String phone = etPhoneNumber.getText().toString().trim();
                sendVerifyCode(phone);
                //显示等待Dialog
                dialog.show();
            }
        });

        //手机号输入框监听
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 11) {
                    btnGetAuth.setEnabled(true);
                } else {
                    btnGetAuth.setEnabled(false);
                }
            }
        });

        //注册
        btnStartPhoneRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //初始化Dialog
        dialog = new MaterialDialog.Builder(getContext())
                .customView(R.layout.dialog_wait, true)
                .build();
    }

    /**
     * 验证码发送结果回调
     *
     * @param event
     */
    public void onEventMainThread(AuthCodeSendResultEvent event) {
        if (event.isSuccess()) {
            //TODO---显示---验证码输入框---确定框---密码输入框
            ToastHelper.show(getContext(), "验证码发送成功");
            etAuth.setVisibility(View.VISIBLE);
            btnStartPhoneRegister.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
        } else {
            //TODO---提示发送出错
            ToastHelper.show(getContext(), "验证码发送失败");
        }
        Timber.e("收到回调.......");
        //隐藏dialog
        dialog.dismiss();
    }

    /**
     * 注册结果回调
     * @param event
     */
    public void onEventMainThread(RegisterResultEvent event){
        //判断当前fragment是否响应外界的event
        if(isInUse){
            if(event.isSuccess()){
                ToastHelper.show(getContext(), "注册成功, 正在登陆...");
            }else{
                ToastHelper.show(getContext(), "注册失败");
            }
        }
    }

    /**
     * 处理发送验证码动作
     *
     * @param phone the phone
     */
    private void sendVerifyCode(final String phone) {
        btnGetAuth.setEnabled(false);
        secondLeft = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_UPDATE_BUTTON_TEXT);
            }
        }, 1000, 1000);
        //请求发送验证码
        Timber.e("向" + phone + "发送短信");
        XPGController.getInstance(getContext()).getmCenter().cRequestSendVerifyCode(phone);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

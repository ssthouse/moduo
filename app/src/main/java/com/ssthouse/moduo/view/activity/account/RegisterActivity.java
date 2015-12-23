package com.ssthouse.moduo.view.activity.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.model.event.RegisterActivityDestoryEvent;
import com.ssthouse.moduo.model.event.setting.RegisterFragmentChangeEvent;
import com.ssthouse.moduo.view.fragment.EmailRegisterFragment;
import com.ssthouse.moduo.view.fragment.LoginFragment;
import com.ssthouse.moduo.view.fragment.PhoneRegisterFragment;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 注册/登陆 界面
 * <p>
 * 登陆或者注册成功的回调全部在这个activity里面
 * Created by ssthouse on 2015/12/19.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * fragment
     */
    private EmailRegisterFragment emailRegisterFragment;
    private PhoneRegisterFragment phoneRegisterFragment;
    private LoginFragment loginFragment;

    private FragmentManager fragmentManager;


    /**
     * 启动当前activity
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        initFragment();
        initView();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.id_tb));
        getSupportActionBar().setTitle("登陆");
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        loginFragment = new LoginFragment();
        emailRegisterFragment = new EmailRegisterFragment();
        phoneRegisterFragment = new PhoneRegisterFragment();
        //初始化为login fragment
        FragmentTransaction transation = fragmentManager.beginTransaction();
        transation.replace(R.id.id_fragment_container, loginFragment).commit();
    }

    /**
     * fragment变化事件
     *
     * @param event
     */
    public void onEventMainThread(RegisterFragmentChangeEvent event) {
        FragmentTransaction transation = fragmentManager.beginTransaction();
        switch (event.getNextFragment()) {
            case EMAIL_REGISTER_FRAGMENT:
                transation.replace(R.id.id_fragment_container, emailRegisterFragment).commit();
                getSupportActionBar().setTitle("注册");
                break;
            case LOGIN_FRAGMENT:
                transation.replace(R.id.id_fragment_container, loginFragment).commit();
                getSupportActionBar().setTitle("登陆");
                break;
            case PHONE_REGISTER_FRAGMENT:
                transation.replace(R.id.id_fragment_container, phoneRegisterFragment).commit();
                getSupportActionBar().setTitle("注册");
                break;
        }
    }

    /**
     * 销毁当前activity的时间
     *
     * @param event
     */
    public void onEventMainThread(RegisterActivityDestoryEvent event) {
        Timber.e("退出register activity");
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

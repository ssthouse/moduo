package com.ssthouse.moduo.fragment.gesture;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.gesture.LockPatternView;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.model.event.view.GestureLockFinishEvent;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 新建手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class NewGestureFragment extends Fragment {

    private State currentState = State.STATE_INPUT_NEW;

    private LockPatternView lockView;
    private TextView tvTip;
    private TextView tvRedraw;
    private TextView tvConfirm;

    private AlertDialog waitDialog;
    private View waitDialogView;

    /**
     * 当前选中的点String表示
     */
    private String currentPatternStr;

    /**
     * 表示当前fragment状态
     */
    private enum State {
        /**
         * 新建密码
         */
        STATE_INPUT_NEW,
        /**
         * 确认密码
         */
        STATE_CONFIRM_NEW
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_gesture, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        tvTip = (TextView) rootView.findViewById(R.id.id_tv_tip);

        tvRedraw = (TextView) rootView.findViewById(R.id.id_btn_redraw);
        tvRedraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回复初始状态
                currentState = State.STATE_INPUT_NEW;
                lockView.clearPattern();
                //tip文字
                tvTip.setText("至少需连接四个点, 请重试");
                tvRedraw.setEnabled(false);
            }
        });

        tvConfirm = (TextView) rootView.findViewById(R.id.id_btn_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected(getContext())) {
                    ToastHelper.show(getContext(), "当前网络未连接");
                    return;
                }
                if (currentPatternStr == null) {
                    ToastHelper.show(getContext(), "图形密码不可为空");
                    return;
                }
                //todo---只有提交成功了---才能退出---将新的用户数据提交到云端
                showWaitDialog("正在上传新的图新密码, 请稍候");
                CloudUtil.getUserInfoObject(SettingManager.getInstance(getContext()).getUserName())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<AVObject>() {
                            @Override
                            public void call(AVObject avObject) {
                                //用户在云端不存在
                                if (avObject == null) {
                                    ToastHelper.show(getContext(), "密码上传云端失败");
                                    waitDialog.dismiss();
                                    return;
                                }
                                avObject.put(CloudUtil.KEY_GESTURE_PASSWORD, currentPatternStr);
                                avObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        waitDialog.dismiss();
                                        if (e == null) {
                                            ToastHelper.show(getContext(), "密码修改成功");
                                            //将密码同步到本地
                                            SettingManager.getInstance(getContext()).setGestureLock(currentPatternStr);
                                            //退出activity
                                            EventBus.getDefault().post(new GestureLockFinishEvent());
                                        } else {
                                            ToastHelper.show(getContext(), "密码上传云端失败");
                                        }
                                    }
                                });
                            }
                        });
            }
        });

        lockView = (LockPatternView) rootView.findViewById(R.id.id_gesture_lock);

        lockView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                tvTip.setText("完成后松开手指");
            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                //第一处输入
                if (currentState == State.STATE_INPUT_NEW) {
                    //小于四个点
                    if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
                        tvTip.setText("至少需连接四个点,请重试");
                        lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                        return;
                    }
                    //跳转到---再次输入确认状态
                    currentPatternStr = LockPatternView.patternToString(pattern);
                    currentState = State.STATE_CONFIRM_NEW;
                    tvRedraw.setEnabled(true);
                    //清空lockpattern
                    lockView.clearPattern();
                    //提示再次输入确认密码
                    tvTip.setText("再次输入确认图形密码");
                }
                //第二次输入----确认输入
                else if (currentState == State.STATE_CONFIRM_NEW) {
                    //少于四个点
                    if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
                        tvTip.setText("至少需连接四个点,请重试");
                        lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                        return;
                    }
                    //和第一次不一样
                    if (!currentPatternStr.equals(LockPatternView.patternToString(pattern))) {
                        //隐藏确认按钮
                        tvConfirm.setEnabled(false);
                        tvTip.setText("请重试");
                        //gesture变红
                        lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                        return;
                    }
                    //输入正确---显示确认按钮
                    tvConfirm.setEnabled(true);
                }
            }
        });

        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }
}
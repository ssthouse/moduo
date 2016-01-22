package com.ssthouse.moduo.main.view.fragment.gesture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ssthouse.gesture.LockPatternView;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.control.util.CloudUtil;
import com.ssthouse.moduo.main.control.util.ToastHelper;
import com.ssthouse.moduo.main.control.xpg.SettingManager;

import java.util.List;

/**
 * 新建手势密码
 * Created by ssthouse on 2016/1/16.
 */
public class NewGestureFragment extends Fragment {

    private State currentState = State.STATE_INPUT_NEW;

    private LockPatternView lockView;
    private TextView tvTip;
    private Button btnRedraw;
    private Button btnConfirm;

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

        btnRedraw = (Button) rootView.findViewById(R.id.id_btn_redraw);
        btnRedraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回复初始状态
                currentState = State.STATE_INPUT_NEW;
                lockView.clearPattern();
                //tip文字
                tvTip.setText("绘制图形密码,请连接至少4个点");
                btnRedraw.setVisibility(View.INVISIBLE);
            }
        });

        btnConfirm = (Button) rootView.findViewById(R.id.id_btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPatternStr != null) {
                    SettingManager.getInstance(getContext()).setGestureLock(currentPatternStr);
                    //将新的用户数据提交到云端
                    CloudUtil.updateUserInfoToCloud(SettingManager.getInstance(getContext()).getCurrentUserInfo());
                    ToastHelper.show(getContext(), "图形密码设置成功");
                    getActivity().finish();
                }
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
                if (currentState == State.STATE_INPUT_NEW) {
                    //检测到输入
                    if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
                        tvTip.setText("至少需连接四个点,请重试");
                        lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    } else {
                        //跳转到---再次输入确认状态
                        currentPatternStr = LockPatternView.patternToString(pattern);
                        currentState = State.STATE_CONFIRM_NEW;
                        btnRedraw.setVisibility(View.VISIBLE);
                        //清空lockpattern
                        lockView.clearPattern();
                        //提示再次输入确认密码
                        tvTip.setText("再次输入确认图形密码");
                    }
                } else if (currentState == State.STATE_CONFIRM_NEW) {
                    if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
                        tvTip.setText("至少需连接四个点,请重试");
                        lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    } else {
                        if (currentPatternStr.equals(LockPatternView.patternToString(pattern))) {
                            //显示确认按钮
                            btnConfirm.setVisibility(View.VISIBLE);
                        } else {
                            //隐藏确认按钮
                            btnConfirm.setVisibility(View.INVISIBLE);
                            tvTip.setText("请重试");
                        }
                    }
                }
            }
        });
    }
}

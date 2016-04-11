package com.ssthouse.moduo.fragment.gesture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssthouse.gesture.LockPatternView;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.account.GestureLockActivity;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.SettingManager;

import java.util.List;

/**
 * 验证图形密码Fragment:
 * 只有图形密码验证成功了, 发出跳转到NewGestureFragment的事件
 * Created by ssthouse on 2016/1/19.
 */
public class ConfirmGestureFragment extends Fragment {

    private TextView tvTip;

    private LockPatternView lockView;

    private TextView tvForgetGesture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_gesture, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        tvTip = (TextView) rootView.findViewById(R.id.id_tv_tip);

        tvForgetGesture = (TextView) rootView.findViewById(R.id.id_btn_forget_gesture);
        tvForgetGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/1/19 输入密码对话框
                Toast.showOnCoding();
            }
        });

        lockView = (LockPatternView) rootView.findViewById(R.id.id_gesture_lock);
        lockView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                //手势是否正确
                if (!LockPatternView.patternToString(pattern)
                        .equals(SettingManager.getInstance(getContext()).getGestureLock())) {
                    tvTip.setText("请重试");
                    lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                } else {
                    Toast.show("手势认证成功");
                    GestureLockActivity activity = (GestureLockActivity) getActivity();
                    activity.toNewGestureFragment();
                }
            }
        });
    }
}

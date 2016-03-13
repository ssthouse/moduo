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
 * 编辑图形密码:
 * 只需要确认你知不知道密码就好---确认后---跳转到newGestureFragment就好
 * Created by ssthouse on 2016/1/19.
 */
public class EditGestureFragment extends Fragment {

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
                if(!LockPatternView.patternToString(pattern)
                        .equals(SettingManager.getInstance(getContext()).getGestureLock())){
                    tvTip.setText("请重试");
                    lockView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                }else{
                    GestureLockActivity activity = (GestureLockActivity) getActivity();
                    activity.toNewGestureFragment();
                }
            }
        });
    }
}

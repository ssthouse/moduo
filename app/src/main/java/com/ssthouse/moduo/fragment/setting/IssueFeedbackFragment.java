package com.ssthouse.moduo.fragment.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.SettingActivity;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.model.event.view.SettingAtyStateEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 问题反馈fragment
 * Created by ssthouse on 2016/1/26.
 */
public class IssueFeedbackFragment extends Fragment {

    @Bind(R.id.id_et_contact_info)
    EditText etContactInfo;

    @Bind(R.id.id_et_issue_content)
    EditText etIssueContent;

    @Bind(R.id.id_tv_submit)
    TextView tvSubmit;

    private Dialog waitDialog;
    private View waitDialogView;
    private Dialog confirmDialog;
    private View confirmDialogView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_issue_feedback, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        initDialog();
        return rootView;
    }

    private void initView() {
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected(getContext())) {
                    Toast.show("当前网络不可用");
                    return;
                }
                if (TextUtils.isEmpty(etIssueContent.getText())) {
                    Toast.show("请写下对我们建议和意见");
                    return;
                }
                //将信息上传至leancloud
                AVObject issueObject = new AVObject(CloudUtil.TABLE_ISSUE_FEEDBACK);
                issueObject.put(CloudUtil.KEY_CONTACT_INFO, etContactInfo.getText().toString());
                issueObject.put(CloudUtil.KEY_ISSUE_CONTENT, etIssueContent.getText().toString());
                issueObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        waitDialog.dismiss();
                        if (e == null) {
                            showConfirmDialog(true);
                        } else {
                            showConfirmDialog(false);
                        }
                    }
                });
                //显示等待dialog
                showWaitDialog("正在提交建议, 请稍候");
            }
        });
    }

    private void initDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        waitDialogView = inflater.inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .setCancelable(false)
                .create();

        confirmDialogView = inflater.inflate(R.layout.dialog_wait_confirm, null);
        confirmDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(confirmDialogView)
                .setCancelable(false)
                .create();
    }

    private void showWaitDialog(String msg) {
        TextView tvWait = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvWait.setText(msg);
        waitDialog.show();
    }

    private void showConfirmDialog(boolean success) {
        TextView tvConfirmContent = (TextView) confirmDialogView.findViewById(R.id.id_tv_content);
        if (success) {
            tvConfirmContent.setText("意见提交成功, 感谢您的意见!");
        } else {
            tvConfirmContent.setText("意见提交失败");
        }
        //点击事件
        confirmDialogView.findViewById(R.id.id_tv_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        //返回列表fragment
                        EventBus.getDefault().post(new SettingAtyStateEvent(SettingActivity.State.STATE_SETTING_LIST));
                    }
                });
        confirmDialogView.findViewById(R.id.id_iv_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        //返回列表fragment
                        EventBus.getDefault().post(new SettingAtyStateEvent(SettingActivity.State.STATE_SETTING_LIST));
                    }
                });
        confirmDialog.show();
    }
}
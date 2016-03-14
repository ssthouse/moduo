package com.ssthouse.moduo.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.activity.SettingActivity;
import com.ssthouse.moduo.activity.account.GestureLockActivity;
import com.ssthouse.moduo.control.util.NetUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.model.event.view.SettingAtyStateEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 设置列表fragment
 * Created by ssthouse on 2016/1/26.
 */
public class SettingListFragment extends Fragment {

    //lv数据
    private String lvEntity[] = {"常见问题", "问题反馈", "使用帮助", "使用条款", "图形密码"};

    @Bind(R.id.id_lv_setting)
    ListView mlv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_list, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        mlv.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.item_setting, R.id.id_tv_content, lvEntity));

        mlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //常见问题
                    case 0:
                        //发出跳转常见问题Fragment event
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_COMMON_ISSUE));
                        break;
                    //问题反馈
                    case 1:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_ISSUE_FEEDBACK));
                        break;
                    //使用帮助
                    case 2:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_USING_HELP));
                        break;
                    //使用条款
                    case 3:
                        EventBus.getDefault()
                                .post(new SettingAtyStateEvent(SettingActivity.State.STATE_USER_TERM));
                        break;
                    //图形密码
                    case 4:
                        if (!NetUtil.isConnected(getContext())) {
                            Toast.show("当前无网络连接");
                            return;
                        }
                        //跳转图形密码设置activity
                        GestureLockActivity.start(getActivity());
                        break;
                }
            }
        });
    }
}

package com.ssthouse.moduo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.ActivityUtil;
import com.ssthouse.moduo.control.util.ToastHelper;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.event.xpg.GetBoundDeviceEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 选择魔哆Fragment
 * Created by ssthouse on 2016/2/29.
 */
public class SwitchModuoFragment extends Fragment {

    @Bind(R.id.id_lv)
    ListView lv;


    //loading层UI
    @Bind(R.id.id_ll_loading)
    LinearLayout llLoading;

    @Bind(R.id.id_pb)
    ProgressBar pb;

    @Bind(R.id.id_tv_tip)
    TextView tvTip;

    @Bind(R.id.id_iv_moduo)
    ImageView ivModuo;

    //list---数据列表
    private List<XPGWifiDevice> xpgWifiDeviceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch_moduo, container, false);
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        initView();
        showLoading();
        //加载设备列表
        getDeviceList();
        return rootView;
    }

    private void initView() {
        lv.setAdapter(adapter);

        //重新加载设备点击事件
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceList();
            }
        };
        ivModuo.setOnClickListener(listener);
        tvTip.setOnClickListener(listener);
    }

    private void getDeviceList() {
        //获取设备数据
        SettingManager settingManager = SettingManager.getInstance(getContext());
        XPGController.getInstance(getContext()).getmCenter().cGetBoundDevices(
                settingManager.getUid(), settingManager.getToken()
        );
    }

    //show loading界面
    private void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        ivModuo.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvTip.setText("正在加载设备列表");
    }

    //show加载失败UI
    public void showLoadErr() {
        llLoading.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        ivModuo.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvTip.setText("加载设备列表失败, 点击重试");
    }

    //show设备列表UI
    public void showDeviceList() {
        llLoading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    //获取设备列表回调
    public void onEventMainThread(GetBoundDeviceEvent event) {
        if (!ActivityUtil.isTopActivity(getActivity(), "SwitchModuoActivity")) {
            return;
        }
        if (!event.isSuccess() || event.getXpgDeviceList() == null) {
            ToastHelper.show(getContext(), "获取设备列表失败");
            showLoadErr();
            return;
        }
        if (event.getXpgDeviceList().size() == 0) {
            ToastHelper.show(getContext(), "当前未绑定魔哆设备");
            showLoadErr();
            return;
        }
        //更新当前设备列表
        xpgWifiDeviceList = event.getXpgDeviceList();
        showDeviceList();
    }

    private BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return xpgWifiDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_switch_moduo, parent, false);
            TextView tvRemark = (TextView) convertView.findViewById(R.id.id_tv_moduo_remark);
            tvRemark.setText(xpgWifiDeviceList.get(position).getRemark());
            return convertView;
        }
    };
}

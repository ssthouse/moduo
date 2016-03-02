package com.ssthouse.moduo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
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
import com.ssthouse.moduo.model.event.xpg.DeviceBindResultEvent;
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

    //正常主界面列表
    @Bind(R.id.id_lv)
    ListView lv;

    //loading层UI
    @Bind(R.id.id_ll_loading)
    LinearLayout llLoading;
    //加载条
    @Bind(R.id.id_pb)
    ProgressBar pb;
    //提示文字
    @Bind(R.id.id_tv_tip)
    TextView tvTip;
    //魔哆图像
    @Bind(R.id.id_iv_moduo)
    ImageView ivModuo;

    //改变remark的dialog
    private View changeRemarkDialogView;
    private Dialog changeRemarkDialog;

    //等待dialog
    private View waitDialogView;
    private Dialog waitDialog;

    //当前长按的position
    private int longClickPosition;

    //list---数据列表
    private List<XPGWifiDevice> xpgWifiDeviceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch_moduo, container, false);
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        initView();
        initDialog();
        showLoading();
        //加载设备列表
        getDeviceList();
        return rootView;
    }

    private void initView() {
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickPosition = position;
                showChangeRemarkDialog();
                return true;
            }
        });

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

    private void initDialog() {
        changeRemarkDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_moduo_remark, null);
        //小叉
        changeRemarkDialogView.findViewById(R.id.id_iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRemarkDialog.dismiss();
            }
        });

        //remark文字
        final EditText etRemark = (EditText) changeRemarkDialogView.findViewById(R.id.id_et_moduo_remark);

        //确认按钮
        changeRemarkDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有写备注
                if (TextUtils.isEmpty(etRemark.getText())) {
                    ToastHelper.show(getContext(), "请填写备注");
                    return;
                }
                //备注和之前是一样的
                if (etRemark.getText().toString().equals(xpgWifiDeviceList.get(longClickPosition).getRemark())) {
                    ToastHelper.show(getContext(), "备注未变化");
                    return;
                }
                //改变备注--重新绑定
                SettingManager settingManager = SettingManager.getInstance(getContext());
                XPGController.getInstance(getContext())
                        .getmCenter()
                        .cBindDevice(settingManager.getUid(),
                                settingManager.getToken(),
                                xpgWifiDeviceList.get(longClickPosition).getDid(),
                                xpgWifiDeviceList.get(longClickPosition).getPasscode(),
                                etRemark.getText().toString());
                //隐藏当前dialog---显示waitDialog
                changeRemarkDialog.dismiss();
                waitDialog.show();
            }
        });
        changeRemarkDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(changeRemarkDialogView)
                .create();

        //等待dialog
        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        TextView tvTip = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvTip.setText("正在更改备注, 请稍候");
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);
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

    //弹出修改备注dialog
    private void showChangeRemarkDialog() {
        changeRemarkDialog.show();
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

    //绑定设备回调
    public void onEventMainThread(DeviceBindResultEvent event) {
        if (!ActivityUtil.isTopActivity((Activity) getContext(), "SwitchModuoActivity")) {
            return;
        }
        //修改备注后---重新绑定失败
        if (!event.isSuccess()) {
            ToastHelper.show(getContext(), "备注修改失败, 请稍候重试");
            changeRemarkDialog.dismiss();
            return;
        }
        waitDialog.dismiss();
        ToastHelper.show(getContext(), "备注修改成功");
        //退出Activity
        getActivity().finish();
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

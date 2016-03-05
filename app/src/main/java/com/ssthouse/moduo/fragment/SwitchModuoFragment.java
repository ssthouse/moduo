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
import com.ssthouse.moduo.model.event.xpg.XpgDeviceLoginEvent;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

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

    //确认更改魔哆Dialog
    private View sureChangeDialogView;
    private Dialog sureChangeDialog;

    //当前长按的position
    private int currentLongClickPosition;
    //当前点按的position
    private int currentClickPosition;
    //当前设备position
    private int currentModuoPosition = -1;

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
        //长按编辑remark
        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentLongClickPosition = position;
                showChangeRemarkDialog();
                return true;
            }
        });

        //点按切换魔哆
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == currentModuoPosition) {
                    return;
                }
                //弹出确认switch对话框
                currentClickPosition = position;
                showConfirmSwitchDialog();
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
                if (etRemark.getText().toString().equals(xpgWifiDeviceList.get(currentLongClickPosition).getRemark())) {
                    ToastHelper.show(getContext(), "备注未变化");
                    return;
                }
                //改变备注--重新绑定
                SettingManager settingManager = SettingManager.getInstance(getContext());
                XPGController.getInstance(getContext())
                        .getmCenter()
                        .cBindDevice(settingManager.getUid(),
                                settingManager.getToken(),
                                xpgWifiDeviceList.get(currentLongClickPosition).getDid(),
                                xpgWifiDeviceList.get(currentLongClickPosition).getPasscode(),
                                etRemark.getText().toString());
                //隐藏当前dialog---显示waitDialog
                changeRemarkDialog.dismiss();
                showWaitDialog("正在更改备注, 请稍候");
            }
        });
        changeRemarkDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(changeRemarkDialogView)
                .create();

        //等待dialog
        waitDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_wait, null);
        waitDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(waitDialogView)
                .create();
        waitDialog.setCanceledOnTouchOutside(false);

        //确认更换魔哆Dialog
        sureChangeDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_msg_confirm, null);
        sureChangeDialogView.findViewById(R.id.id_iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureChangeDialog.dismiss();
            }
        });
        sureChangeDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏confirmDialog   显示waitDialog
                sureChangeDialog.dismiss();
                showWaitDialog("正在切换魔哆, 请稍候");
                SettingManager settingManager = SettingManager.getInstance(getContext());
                //登陆当前选中的魔哆
                xpgWifiDeviceList.get(currentClickPosition).login(
                        settingManager.getUid(),
                        settingManager.getToken()
                );
                //todo---改变本地的moduo数据
            }
        });
        sureChangeDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(sureChangeDialogView)
                .create();
        sureChangeDialog.setCanceledOnTouchOutside(false);
    }

    //等待dialog
    private void showWaitDialog(String msg) {
        TextView tvTip = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvTip.setText(msg);
        waitDialog.show();
    }

    private void showConfirmSwitchDialog() {
        TextView tvContent = (TextView) sureChangeDialogView.findViewById(R.id.id_tv_content);
        tvContent.setText("确认切换当前魔哆设备吗?");
        sureChangeDialog.show();
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

    //设备登陆结果回调
    public void onEventMainThread(XpgDeviceLoginEvent event) {
        waitDialog.dismiss();
        //登陆成功---且是当前点击的设备
        if (xpgWifiDeviceList.size() == 0) {
            Timber.e("xogDeviceList is not initialed");
            return;
        }
        if (event.isSuccess() && event.getDid().equals(xpgWifiDeviceList.get(currentClickPosition).getDid())) {
            showLoading();
            getDeviceList();
        }
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
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_switch_moduo, parent, false);
            TextView tvRemark = (TextView) rootView.findViewById(R.id.id_tv_moduo_remark);
            tvRemark.setText(xpgWifiDeviceList.get(position).getRemark());
            //标记当前的设备
            // 如果当前没有wifi设备连接
            if (XPGController.getCurrentDevice() != null) {
                String did = xpgWifiDeviceList.get(position).getDid();
                String currentDid = XPGController.getCurrentDevice().getXpgWifiDevice().getDid();
                if (did.equals(currentDid)) {
                    Timber.e("我设置了选中item" + position);
                    Timber.e(did);
                    rootView.findViewById(R.id.id_ll_item_container).setBackgroundColor(0xaaeeeeee);
                    //设置当前魔哆position
                    currentModuoPosition = position;
                }
            }
            return rootView;
        }
    };
}

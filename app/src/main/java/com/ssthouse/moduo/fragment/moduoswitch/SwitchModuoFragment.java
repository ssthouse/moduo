package com.ssthouse.moduo.fragment.moduoswitch;

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
import com.ssthouse.moduo.activity.SwitchModuoActivity;
import com.ssthouse.moduo.control.util.CloudUtil;
import com.ssthouse.moduo.control.util.Toast;
import com.ssthouse.moduo.control.xpg.CmdCenter;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.ModuoInfo;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 切换魔哆Fragment
 * Created by ssthouse on 2016/2/29.
 */
public class SwitchModuoFragment extends Fragment implements SwitchFragmentView {

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

    private BaseAdapter adapter;

    //item操作选择Dialog
    private View optionsDialogView;
    private Dialog optionsDialog;

    //改变remark的dialog
    private View changeRemarkDialogView;
    private Dialog changeRemarkDialog;

    //等待dialog
    private View waitDialogView;
    private Dialog waitDialog;

    //确认更改魔哆Dialog
    private View confirmChangeDialogView;
    private Dialog confirmChangeDialog;

    //Presenter
    private SwitchModuoPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_switch_moduo, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter = new SwitchModuoPresenter(this, getActivity());
        initView();
        initDialog();
        showLoading();
        //加载设备列表
        mPresenter.getDeviceList();
        return rootView;
    }

    private void initView() {
        //长按编辑remark
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return mPresenter.getXpgWifiDeviceList().size();
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
            public View getView(final int position, View convertView, ViewGroup parent) {
                View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_switch_moduo, parent, false);
                //别名
                TextView tvRemark = (TextView) rootView.findViewById(R.id.id_tv_moduo_remark);
                tvRemark.setText(mPresenter.getXpgWifiDeviceList().get(position).getRemark());
                //did
                TextView tvDid = (TextView) rootView.findViewById(R.id.id_tv_moduo_did);
                tvDid.setText("设备号:\t" + mPresenter.getXpgWifiDeviceList().get(position).getDid());
                //编辑remark按钮点击事件
                ImageView ivEdit = (ImageView) rootView.findViewById(R.id.id_iv_edit);
                ivEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.setCurrentLongClickPosition(position);
                        showChangeRemarkDialog();
                    }
                });
                //标记当前的设备
                // 如果当前没有wifi设备连接
                String localDid = SettingManager.getInstance(getContext()).getCurrentDid();
                String currentDid = mPresenter.getXpgWifiDeviceList().get(position).getDid();
                if (!TextUtils.isEmpty(localDid)) {
                    if (localDid.equals(currentDid)) {
                        Timber.e("我设置了选中item" + position);
                        Timber.e(localDid);
                        rootView.findViewById(R.id.id_ll_item_container).setBackgroundColor(0xcceeeeee);
                        //设置当前魔哆position
                        mPresenter.setCurrentModuoPosition(position);
                    }
                }
                return rootView;
            }
        };

        lv.setAdapter(adapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.setCurrentLongClickPosition(position);
                showOptionsDialog();
                return true;
            }
        });

        //点按切换魔哆
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mPresenter.getCurrentModuoPosition()) {
                    return;
                }
                //弹出确认switch对话框
                mPresenter.setCurrentClickPosition(position);
                showConfirmSwitchDialog();
            }
        });

        //重新加载设备点击事件
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getDeviceList();
            }
        };
        ivModuo.setOnClickListener(listener);
        tvTip.setOnClickListener(listener);
    }

    private void initDialog() {
        //options Dialog
        optionsDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_options_moduo_item, null);
        optionsDialogView.findViewById(R.id.id_tv_change_remark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideOptionsDialog();
                //改名Dialog
                showChangeRemarkDialog();
            }
        });
        optionsDialogView.findViewById(R.id.id_tv_unbind_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏dialog
                hideOptionsDialog();
                //删除设备
                showWaitDialog("正在删除设备...");
                //设备解绑
                SettingManager settingManager = SettingManager.getInstance(getContext());
                XPGWifiDevice xpgWifiDevice = mPresenter.getXpgWifiDeviceList().get(mPresenter.getCurrentLongClickPosition());
                CmdCenter.getInstance(getContext()).cUnbindDevice(settingManager.getUid(),
                        settingManager.getToken(),
                        xpgWifiDevice.getDid(),
                        xpgWifiDevice.getPasscode()
                );
            }
        });
        optionsDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(optionsDialogView)
                .create();

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
                    Toast.show("请填写备注");
                    return;
                }
                //备注和之前是一样的
                if (etRemark.getText().toString().equals(mPresenter.getXpgWifiDeviceList().get(mPresenter.getCurrentClickPosition()).getRemark())) {
                    Toast.show("备注未变化");
                    return;
                }
                //改变备注--重新绑定
                XPGWifiDevice currentDevice = mPresenter.getXpgWifiDeviceList().get(mPresenter.getCurrentLongClickPosition());
                SettingManager settingManager = SettingManager.getInstance(getContext());
                XPGController.getInstance(getContext())
                        .getmCenter()
                        .cBindDevice(settingManager.getUid(),
                                settingManager.getToken(),
                                currentDevice.getDid(),
                                currentDevice.getPasscode(),
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
        confirmChangeDialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_msg_confirm, null);
        confirmChangeDialogView.findViewById(R.id.id_iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmChangeDialog.dismiss();
            }
        });
        confirmChangeDialogView.findViewById(R.id.id_tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //隐藏confirmDialog   显示waitDialog
                confirmChangeDialog.dismiss();
                showWaitDialog("正在切换魔哆, 请稍候");
                //将当前设备设为本地的魔哆--然后重新获取所有设备
                final SettingManager settingManager = SettingManager.getInstance(getContext());
                XPGWifiDevice xpgWifiDevice = mPresenter.getXpgWifiDeviceList().get(mPresenter.getCurrentClickPosition());
                CloudUtil.getDeviceFromCloud(xpgWifiDevice.getDid())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ModuoInfo>() {
                            @Override
                            public void call(ModuoInfo moduoInfo) {
                                if (moduoInfo == null) {
                                    //从服务器端获取该设备信息失败
                                    Toast.show("服务器获取设备信息失败, 请稍后重试");
                                } else {
                                    //通知SwitchFragmentActivity---魔哆发生了切换
                                    ((SwitchModuoActivity) getActivity()).setIsModuoSwitched(true);
                                    //将当前魔哆信息保存到本地
                                    settingManager.setCurrentModuoInfo(moduoInfo);
                                    //获取所有魔哆设备
                                    XPGController.getInstance(getContext()).getmCenter().cGetBoundDevices(
                                            settingManager.getUid(),
                                            settingManager.getToken()
                                    );
                                }
                            }
                        });
            }
        });
        confirmChangeDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                .setView(confirmChangeDialogView)
                .create();
        confirmChangeDialog.setCanceledOnTouchOutside(false);
    }

    //等待dialog
    @Override
    public void showWaitDialog(String msg) {
        TextView tvTip = (TextView) waitDialogView.findViewById(R.id.id_tv_wait);
        tvTip.setText(msg);
        waitDialog.show();
    }

    @Override
    public void dismissWaitDialog() {
        waitDialog.dismiss();
    }

    @Override
    public void showOptionsDialog() {
        optionsDialog.show();
    }

    @Override
    public void hideOptionsDialog() {
        optionsDialog.hide();
    }

    @Override
    public void showConfirmSwitchDialog() {
        TextView tvContent = (TextView) confirmChangeDialogView.findViewById(R.id.id_tv_content);
        tvContent.setText("确认切换当前魔哆设备吗?");
        confirmChangeDialog.show();
    }

    @Override
    public void dismissConfirmSwitchDialog() {
        confirmChangeDialog.dismiss();
    }

    //show loading界面
    @Override
    public void showLoading() {
        llLoading.setVisibility(View.VISIBLE);
        ivModuo.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvTip.setText("正在加载设备列表");
    }

    //show加载失败UI
    @Override
    public void showLoadErr() {
        llLoading.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
        ivModuo.setVisibility(View.VISIBLE);
        tvTip.setVisibility(View.VISIBLE);
        tvTip.setText("加载设备列表失败, 点击重试");
    }

    //show设备列表UI
    @Override
    public void showDeviceList() {
        llLoading.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    //弹出修改备注dialog
    @Override
    public void showChangeRemarkDialog() {
        changeRemarkDialog.show();
    }

    @Override
    public void dismissChangeRemarkDialog() {
        changeRemarkDialog.dismiss();
    }
}

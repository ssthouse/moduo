package com.ssthouse.moduo.fragment.sliding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.xpg.SettingManager;
import com.ssthouse.moduo.control.xpg.XPGController;
import com.ssthouse.moduo.model.bean.device.Device;

/**
 * 魔哆参数展示fragment
 * Created by ssthouse on 2016/1/13.
 */
public class AboutModuoFragment extends Fragment implements IFragmentUI {

    private String[] args = {"型号", "IP", "设备号", "版本", "备注", "摄像头编号"};
    private String[] values = new String[]{"", "", "", "", "", "", ""};

    private ListView mLv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_moduo, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        //初始化列表内容
        mLv = (ListView) rootView.findViewById(R.id.id_lv_setting);
        mLv.setAdapter(mAdapter);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return args.length;
        }

        @Override
        public Object getItem(int position) {
            return args[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View itemView = inflater.inflate(R.layout.item_about_moduo, parent, false);
            TextView tvTitle = (TextView) itemView.findViewById(R.id.id_tv_title);
            tvTitle.setText(args[position]);
            TextView tvContent = (TextView) itemView.findViewById(R.id.id_tv_content);
            tvContent.setText(values[position]);
            return itemView;
        }
    };

    @Override
    public void updateUI() {
        //获取新的设备数据
        if (XPGController.getCurrentDevice() == null) {
            //Timber.e("当前没有设备连接");
            return;
        }
        Device device = XPGController.getCurrentDevice();
        //产品名称
        values[0] = device.getXpgWifiDevice().getProductName();
        //网络ip
        values[1] = device.getXpgWifiDevice().getIPAddress();
        //设备号
        values[2] = device.getXpgWifiDevice().getDid();
        //Mac地址
        values[3] = device.getXpgWifiDevice().getMacAddress();
        //备注
        values[4] = device.getXpgWifiDevice().getRemark();
        //视频CID
        values[5] = SettingManager.getInstance(getContext()).getCidNumber();
        //刷新UI
        mAdapter.notifyDataSetChanged();
    }
}

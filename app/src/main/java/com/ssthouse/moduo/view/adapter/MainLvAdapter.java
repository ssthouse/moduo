package com.ssthouse.moduo.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.model.Device;
import com.ssthouse.moduo.view.activity.VideoActivity;

import java.util.List;

/**
 * 主界面listview的adapter
 * Created by ssthouse on 2015/12/17.
 */
public class MainLvAdapter extends BaseAdapter {

    private Context context;

    /**
     * 和主界面中deviceList是一个
     */
    private List<Device> deviceList;

    /**
     * 构造方法
     *
     * @param context
     */
    public MainLvAdapter(Context context, List<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    /**
     * 刷新数据
     */
    public void update() {
        //deviceList = PreferenceHelper.getInstance(context).getDeviceList();
        notifyDataSetInvalidated();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.view_device_card, null);
            viewHolder.tvCid = (TextView) convertView.findViewById(R.id.id_tv_cid_name);
            viewHolder.tvDeviceState = (TextView) convertView.findViewById(R.id.id_tv_device_state);
            viewHolder.btnStartVideo = (Button) convertView.findViewById(R.id.id_btn_start_video);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //配置触发事件
        viewHolder.tvCid.setText("设备Cid号码:" + deviceList.get(position).getCidNumber() + "");
        //加载视频sdk状态
        String stateStr = "离线";
        boolean isVideoEnable = false;
        switch (deviceList.get(position).getStreamerPresenceState()) {
            case INIT:
                stateStr = "正在初始化";
                isVideoEnable = false;
                break;
            case OFFLINE:
                stateStr = "离线";
                isVideoEnable = false;
                break;
            case ONLINE:
                stateStr = "在线";
                isVideoEnable = true;
                break;
            case USRNAME_PWD_ERR:
                stateStr = "用户名或密码错误";
                isVideoEnable = false;
                break;
        }
        viewHolder.tvDeviceState.setText(stateStr);
        viewHolder.btnStartVideo.setEnabled(isVideoEnable);
        //视频开关点击事件
        viewHolder.btnStartVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---启动video activity
                VideoActivity.start(context, deviceList.get(position).getCidNumber());
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public TextView tvCid;
        public TextView tvDeviceState;
        public Button btnStartVideo;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

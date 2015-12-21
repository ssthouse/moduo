package com.ssthouse.moduo.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.setting.XPGController;
import com.ssthouse.moduo.model.Device;
import com.ssthouse.moduo.view.activity.VideoActivity;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

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
        notifyDataSetInvalidated();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.view_device_card, null);
            //video
            viewHolder.tvCid = (TextView) convertView.findViewById(R.id.id_tv_cid_name);
            viewHolder.tvCameraState = (TextView) convertView.findViewById(R.id.id_tv_camera_state);
            viewHolder.btnStartVideo = (Button) convertView.findViewById(R.id.id_btn_start_video);
            //xpg
            viewHolder.tvXpgDeviceState = (TextView) convertView.findViewById(R.id.id_tv_xpg_state);
            viewHolder.btnXpgControlStart = (Button) convertView.findViewById(R.id.id_btn_xpg_control);
            //参数设置
            viewHolder.ibDeviceInfoStart = (ImageButton) convertView.findViewById(R.id.id_ib_info_setting);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //初始化视频对话事件
        initVideoEvent(viewHolder, position);
        //初始化xpg管理事件
        initXpgEvent(viewHolder, position);
        //todo---那个imageButton的设备参数设置按钮就先放一放
        return convertView;
    }

    /**
     * 初始化视频对话事件
     *
     * @param viewHolder
     */
    private void initVideoEvent(ViewHolder viewHolder, final int position) {
        //配置触发事件
        viewHolder.tvCid.setText("设备Cid号码:" + deviceList.get(position).getCidNumber() + "");
        //加载视频sdk状态
        String stateStr = "摄像头状态: ";
        boolean isVideoEnable = false;
        switch (deviceList.get(position).getStreamerPresenceState()) {
            case INIT:
                stateStr += "正在初始化";
                isVideoEnable = false;
                break;
            case OFFLINE:
                stateStr += "离线";
                isVideoEnable = false;
                break;
            case ONLINE:
                stateStr += "在线";
                isVideoEnable = true;
                break;
            case USRNAME_PWD_ERR:
                stateStr += "用户名或密码错误";
                isVideoEnable = false;
                break;
        }
        viewHolder.tvCameraState.setText(stateStr);
        viewHolder.btnStartVideo.setEnabled(isVideoEnable);
        //视频开关点击事件
        viewHolder.btnStartVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---启动video activity
                VideoActivity.start(context, deviceList.get(position).getCidNumber());
            }
        });
    }

    /**
     * 初始化xpg管理事件
     *
     * @param viewHolder
     * @param position
     */
    private void initXpgEvent(ViewHolder viewHolder, int position) {
        final XPGWifiDevice xpgWifiDevice = deviceList.get(position).getXpgWifiDevice();
        //设备状态
        String deviceState = "设备状态: ";
        boolean isControlEnable;
        if (xpgWifiDevice.isOnline()) {
            deviceState += "在线";
            isControlEnable = true;
        } else {
            deviceState += "离线";
            isControlEnable = false;
        }
        viewHolder.tvXpgDeviceState.setText(deviceState);
        viewHolder.btnXpgControlStart.setEnabled(isControlEnable);
        viewHolder.btnXpgControlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---启动xpg设备 参数设置 activity
                //设置当前device
                XPGController.setCurrentXpgWifiDevice(xpgWifiDevice);
                //尝试获取该设备数据---启动配置activity
                XPGController.getInstance(context).getmCenter().cGetStatus(xpgWifiDevice);
            }
        });
    }

    private class ViewHolder {
        //设备cid号码
        public TextView tvCid;
        //摄像头状态
        public TextView tvCameraState;
        //开启视频对话按钮
        public Button btnStartVideo;
        //xpg设备状态
        public TextView tvXpgDeviceState;
        //xpg设备控制activity启动按钮
        public Button btnXpgControlStart;
        //设备info参数设置activity启动按钮
        public ImageButton ibDeviceInfoStart;
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

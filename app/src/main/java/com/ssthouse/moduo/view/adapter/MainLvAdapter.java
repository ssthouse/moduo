package com.ssthouse.moduo.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.PreferenceHelper;
import com.ssthouse.moduo.view.activity.VideoActivity;

import java.util.List;

/**
 * 主界面listview的adapter
 * Created by ssthouse on 2015/12/17.
 */
public class MainLvAdapter extends BaseAdapter {

    private Context context;

    private List<String> cidList;

    /**
     * 构造方法
     * @param context
     */
    public MainLvAdapter(Context context) {
        this.context = context;
        this.cidList = PreferenceHelper.getInstance(context).getDeviceCidList();
    }

    /**
     * 刷新数据
     */
    public void update(){
        cidList = PreferenceHelper.getInstance(context).getDeviceCidList();
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return cidList.size();
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
        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.view_device_card, null);
            viewHolder.tvCid = (TextView) convertView.findViewById(R.id.id_tv_cid_name);
            viewHolder.btnStartVideo = (Button) convertView.findViewById(R.id.id_btn_start_video);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //配置触发事件
        viewHolder.tvCid.setText(cidList.get(position));
        viewHolder.btnStartVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---启动video activity
                VideoActivity.start(context, Long.valueOf(cidList.get(position)));
            }
        });
        return convertView;
    }

    private class ViewHolder{
        public TextView tvCid;
        public Button btnStartVideo;
    }
}

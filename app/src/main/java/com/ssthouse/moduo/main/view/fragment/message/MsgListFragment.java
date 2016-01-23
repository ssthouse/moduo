package com.ssthouse.moduo.main.view.fragment.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.main.model.bean.event.view.MsgActivityToDetailEvent;

import de.greenrobot.event.EventBus;

/**
 * 消息列表
 * Created by ssthouse on 2016/1/21.
 */
public class MsgListFragment extends Fragment{

    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg_list, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
        lv = (ListView) rootView.findViewById(R.id.id_lv);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 4;
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
                TextView tv = new TextView(getContext());
                tv.setText("测试");
                return tv;
            }
        });
        
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //// TODO: 2016/1/21  
                EventBus.getDefault().post(new MsgActivityToDetailEvent());
            }
        });
    }
}

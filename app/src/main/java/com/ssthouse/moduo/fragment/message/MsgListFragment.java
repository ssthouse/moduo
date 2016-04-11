package com.ssthouse.moduo.fragment.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.control.util.Toast;

/**
 * 消息列表Fragment
 * Created by ssthouse on 2016/1/21.
 */
public class MsgListFragment extends Fragment {

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
        final LayoutInflater inflater = LayoutInflater.from(getContext());

        lv = (ListView) rootView.findViewById(R.id.id_lv_setting);
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
                ViewHolder viewHolder;
                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_msg_fragment, parent, false);
                    //填充数据
                    viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.id_tv_title);
                    viewHolder.tvContent = (TextView) convertView.findViewById(R.id.id_tv_content);
                    viewHolder.ivReaded = (ImageView) convertView.findViewById(R.id.id_iv_readed);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                //todo---填充View的数据
                //viewHolder.tvTitle.setText("");
                //viewHolder.tvContent.setText("");
                //viewHolder.ivReaded.setImageResource(R.drawable.msg_ic_collect);
                return convertView;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //// TODO: 2016/1/21  ---到时候把整个item发出去
                //EventBus.getDefault().post(new MsgActivityToDetailEvent());
                Toast.show("功能正在开发中");
            }
        });
    }

    class ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        ImageView ivReaded;
    }
}

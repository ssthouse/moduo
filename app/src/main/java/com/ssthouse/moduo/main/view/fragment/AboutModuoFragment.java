package com.ssthouse.moduo.main.view.fragment;

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

/**
 * 关于魔哆
 * Created by ssthouse on 2016/1/13.
 */
public class AboutModuoFragment extends Fragment {

    private String[] args = {"型号", "网络", "总容量", "版本", "序列号"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_moduo, container, false);

        initView(rootView);
        //// TODO: 2016/1/13 尝试获取设备数据信息---然后更新界面

        return rootView;
    }

    private void initView(View rootView) {

        //初始化列表内容
        ListView lv = (ListView) rootView.findViewById(R.id.id_lv);
        lv.setAdapter(new BaseAdapter() {
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
                View itemView = View.inflate(getActivity(), R.layout.item_about_moduo, null);
                TextView tvTitle = (TextView) itemView.findViewById(R.id.id_tv_title);
                tvTitle.setText(args[position]);
                TextView tvContent = (TextView) itemView.findViewById(R.id.id_tv_content);
                tvContent.setText("ssthouse");
                return null;
            }
        });
    }
}

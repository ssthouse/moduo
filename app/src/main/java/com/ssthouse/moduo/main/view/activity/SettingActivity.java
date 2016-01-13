package com.ssthouse.moduo.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ssthouse.moduo.R;

/**
 * 设置
 * Created by ssthouse on 2016/1/13.
 */
public class SettingActivity extends AppCompatActivity {

    private ListView lv;

    private String args[] = {"常见问题", "问题反馈", "使用帮助", "使用条款"};

    /**
     * 启动当前Activity
     * @param context
     */
    public static void start(Context context){
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {

        lv = (ListView) findViewById(R.id.id_lv);
        lv.setAdapter(new ArrayAdapter<>(this, R.layout.item_setting, args));

        //// TODO: 2016/1/13 点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });
    }
}

package com.ssthouse.moduo.view.widget;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.View;

import com.ssthouse.moduo.R;

/**
 * toolbar上的progressbar
 * Created by ssthouse on 2015/12/6.
 */
public class TbProgressProvider extends ActionProvider {

    private Context mContext;

    /**
     * 构造方法
     * @param context
     */
    public TbProgressProvider(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View onCreateActionView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_actionbar_pb, null);

        initView(view);

        return view;
    }

    private void initView(View rootView) {
    }
}

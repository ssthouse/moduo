package com.ssthouse.moduo.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssthouse.moduo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 常见问题fragment
 * Created by ssthouse on 2016/1/26.
 */
public class CommonIssueFragment extends Fragment {

    @Bind(R.id.id_ll_container)
    LinearLayout llContainer;

    private LayoutInflater inflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common_issue, container, false);
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }

    private void initView() {
        //获取所有String数据
        String[] questions = getResources().getStringArray(R.array.common_issue_question);
        String[] answers = getResources().getStringArray(R.array.common_issue_answer);
        //生成所有layout_item---放入Container
        inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < questions.length; i++) {
            View itemView = inflater.inflate(R.layout.item_common_issue, llContainer, false);
            TextView tvQuestion = (TextView) itemView.findViewById(R.id.id_tv_title);
            TextView tvAnswer = (TextView) itemView.findViewById(R.id.id_tv_content);
            tvQuestion.setText(questions[i]);
            tvAnswer.setText(answers[i]);
            llContainer.addView(itemView);
        }
    }
}

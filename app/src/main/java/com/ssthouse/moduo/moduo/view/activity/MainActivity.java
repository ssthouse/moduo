package com.ssthouse.moduo.moduo.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.moduo.view.fragment.ModuoFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    private ModuoFragment moduoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initFragment();
    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        moduoFragment = new ModuoFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.id_fragment_container, moduoFragment)
                .commit();
    }

    private void initView() {
    }

}

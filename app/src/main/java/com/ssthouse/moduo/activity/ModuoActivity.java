package com.ssthouse.moduo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.ssthouse.moduo.R;
import com.ssthouse.moduo.fragment.moduo.view.ModuoFragment;

/**
 * 魔哆对话Activity{
 *     完成魔哆对家具的操控
 * }
 */
public class ModuoActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private ModuoFragment moduoFragment;

    public static void start(Context context){
        Intent intent = new Intent(context, ModuoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moduo);

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

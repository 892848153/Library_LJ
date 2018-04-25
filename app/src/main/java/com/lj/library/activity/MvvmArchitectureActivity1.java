package com.lj.library.activity;

import android.os.Bundle;
import android.os.Handler;

import com.lj.library.R;
import com.lj.library.activity.base.BaseActivity3;
import com.lj.library.databinding.MyArchitecture1ActivityBinding;
import com.lj.library.util.Logger;

import java.util.Random;

/**
 * @author LJ.Liu
 * @date 2018/4/25.
 */
public class MvvmArchitectureActivity1 extends BaseActivity3<MyArchitecture1ActivityBinding> {

    @Override
    protected int initLayout(final Bundle savedInstanceState) {
        return R.layout.my_architecture1_activity;
    }

    @Override
    protected void initComp(final Bundle savedInstanceState) {
        loadContent();
    }

    private void loadContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = new Random().nextInt(2);
                if (i == 1) {
                    Logger.i("show no network layout, random:" + i);
                    showErrorLayout();
                } else {
                    showContentLayout();
                    Logger.i("show content layout, random:" + i);
                }
            }
        }, 2000L);
    }
}

package com.lj.library.activity;

import android.os.Bundle;
import android.os.Handler;

import com.lj.library.R;
import com.lj.library.activity.base.BaseActivity2;
import com.lj.library.databinding.MyArchitecture2ActivityBinding;
import com.lj.library.util.Logger;

import java.util.Random;

/**
 * Created by ocean on 2017/8/30.
 */
public class MvvmArchitectureActivity extends BaseActivity2<MyArchitecture2ActivityBinding>{

    @Override
    protected int initLayout(final Bundle savedInstanceState) {
        return R.layout.my_architecture2_activity;
    }

    @Override
    protected void initComp(final Bundle savedInstanceState) {
        setTitle("Title");
        mBinding.textView.setText("1111111111111111");
        loadContent();
    }

    @Override
    public void retry() {
        super.retry();
        loadContent();
    }

    private void loadContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int i = new Random().nextInt(2);
                if (i == 1) {
                    Logger.i("show no network layout, random:" + i);
                    showLoadingErrorLayout();
                } else {
                    showContentLayout();
                    Logger.i("show content layout, random:" + i);
                }
            }
        }, 2000L);
    }
}

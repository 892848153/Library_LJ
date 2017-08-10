package com.lj.library.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.activity.base.BaseActivity1;
import com.lj.library.util.Logger;

import java.util.Random;

import butterknife.Bind;

/**
 * Created by ocean on 2017/7/13.
 */
public class MyArchitectureActivity extends BaseActivity1 {

    @Bind(R.id.text_view)
    TextView mTextView;

    @Override
    protected int initLayout(final Bundle savedInstanceState) {
        return R.layout.my_architecture_activity;
    }

    @Override
    protected void initComp(final Bundle savedInstanceState) {
        setTitle("Title");
        mTextView.setText("1111111111111111");
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
        }, 2000l);
    }
}

package com.lj.library.fragment.architecture;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.fragment.BaseFragment1;
import com.lj.library.util.Logger;

import java.util.Random;

import butterknife.BindView;

/**
 * Created by ocean on 2017/8/23.
 */
public class MvpArchitectureFragment extends BaseFragment1 {

    @BindView(R.id.text_view)
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

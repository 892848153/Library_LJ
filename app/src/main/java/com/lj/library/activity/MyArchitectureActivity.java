package com.lj.library.activity;

import android.os.Bundle;

import com.lj.library.R;
import com.lj.library.activity.base.BaseActivity1;

/**
 *
 * Created by ocean on 2017/7/13.
 */
public class MyArchitectureActivity extends BaseActivity1 {

    @Override
    protected int initLayout(final Bundle savedInstanceState) {
        return R.layout.banner_fragment;
    }

    @Override
    protected void initComp(final Bundle savedInstanceState) {

    }
}

package com.lj.library.activity.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lj.library.application.MyApplication;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.BaseFragment;

/**
 * 基础类.
 *
 * @author jie.liu
 * @time 2015年3月6日 上午10:55:47
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity implements BackHandlerInterface {

    protected Activity mContext;

    protected BaseFragment mFragmentSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        MyApplication.getInstance().addActivity(mContext);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().removeActivity(mContext);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentSelected == null || !mFragmentSelected.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        mFragmentSelected = selectedFragment;
    }
}

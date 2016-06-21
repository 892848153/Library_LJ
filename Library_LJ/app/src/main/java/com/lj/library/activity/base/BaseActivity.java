package com.lj.library.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lj.library.application.MyApplication;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.BaseFragment;

import butterknife.ButterKnife;

/**
 * 基础类.
 *
 * @author jie.liu
 * @time 2015年3月6日 上午10:55:47
 */
public abstract class BaseActivity extends FragmentActivity implements BackHandlerInterface {

    protected Activity mContext;

    protected BaseFragment mFragmentSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        MyApplication.getInstance().addActivity(mContext);

        /** initViews   **/
        int layoutId = initLayout(savedInstanceState);
        if (layoutId > 0) {
            setContentView(layoutId);
            ButterKnife.bind(this);
            initComp(savedInstanceState);
        }
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

    /**
     * 返回布局id, 供{@link #setContentView(int)}调用.
     *
     * @param savedInstanceState
     * @return
     */
    protected abstract int initLayout(Bundle savedInstanceState);

    /**
     * 初始化组件.
     *
     * @param savedInstanceState
     */
    protected abstract void initComp(Bundle savedInstanceState);
}

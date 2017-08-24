package com.lj.library.activity.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.lj.library.R;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.FragmentBackManager;
import com.lj.library.util.RxBus;
import com.lj.library.util.UIUtils;

import butterknife.ButterKnife;

/**
 * 基础类.
 *
 * @author jie.liu
 * @time 2015年3月6日 上午10:55:47
 */
public abstract class BaseActivity extends AppCompatActivity implements BackHandlerInterface {

    protected Activity mContext;

    protected FragmentBackManager mFragmentSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        MyApplication.getInstance().addActivity(mContext);
        SampleApplicationLike.getInstance().addActivity(mContext);

        /** initViews   **/
        int layoutId = initLayout(savedInstanceState);
        if (layoutId > 0) {
            setContentView(layoutId);
            translucentStatusBar();
            ButterKnife.bind(this);
            initComp(savedInstanceState);
        }
    }

    /**
     * 根据系统版本调整属性ui，实现Translucent app bar
     */
    private void translucentStatusBar() {
        View parentView =((ViewGroup) findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
        View statusBarHolder = findViewById(R.id.status_bar_holder_view);
        // 5.0系统开始可以更改状态栏的颜色，所以不需要状态栏底块
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarHolder.getLayoutParams().height = 0;
            parentView.setFitsSystemWindows(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4系统开始支持布局延伸到状态栏底下，不支持更改状态栏颜色，所以需要一个状态栏底块
            statusBarHolder.getLayoutParams().height = UIUtils.getStatusBarHeight(this);
            parentView.setFitsSystemWindows(false);
        } else {
            statusBarHolder.getLayoutParams().height = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getInstance().removeActivity(mContext);
        SampleApplicationLike.getInstance().removeActivity(mContext);
        RxBus.getInstance().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mFragmentSelected == null || !mFragmentSelected.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(FragmentBackManager selectedFragment) {
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

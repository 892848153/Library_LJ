package com.lj.library.activity.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;
import com.lj.library.util.UIUtils;

import butterknife.ButterKnife;

/**
 * 基础类.
 *
 * @author jie.liu
 * @time 2015年3月6日 上午10:55:47
 */
public abstract class BaseActivity1 extends AppCompatActivity implements BackHandlerInterface, BaseViewAction, View.OnClickListener {

    private static final String TAG = "BaseActivity1";

    protected Activity mContext;

    protected BaseFragment mFragmentSelected;

    private LinearLayout mRootLayout;

    private View mLoadingLayout;

    private View mNoNetworkLayout;

    private View mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SampleApplicationLike.getInstance().addActivity(mContext);

        super.setContentView(R.layout.activity_base);
        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        translucentStatusBar();

        View appBar = initAppBar();
        if (appBar != null && appBar.getParent() == null) {
            int height = (int) (48 * getResources().getDisplayMetrics().density);
            mRootLayout.addView(appBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            View back = appBar.findViewById(R.id.back_iv);
            if (back != null)
                back.setOnClickListener(this);
        }

        mLoadingLayout = initLoadingLayout();
        addViewToRootLayout(mLoadingLayout);

        setContentView(initLayout(savedInstanceState));
        ButterKnife.bind(this);
        initComp(savedInstanceState);
    }

    /**
     * 根据系统版本调整属性ui，实现Translucent app bar
     */
    private void translucentStatusBar() {
        View parentView = ((ViewGroup) findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
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
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        mContentLayout = view;
        mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment) {
        mFragmentSelected = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mFragmentSelected == null || !mFragmentSelected.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SampleApplicationLike.getInstance().removeActivity(mContext);
        RxBus.getInstance().unregister(this);
    }

    protected abstract int initLayout(Bundle savedInstanceState);

    /**
     * 初始化组件.
     *
     * @param savedInstanceState
     */
    protected abstract void initComp(Bundle savedInstanceState);

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.retry_btn:
                retry();
                break;
            default:
        }
    }

    @Override
    public View initAppBar() {
        return View.inflate(mContext, R.layout.include_normal_header, null);
    }

    @Override
    public void setTitle(final int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(final String title) {
        View view = mRootLayout.findViewById(R.id.title_tv);
        if (view != null) {
            TextView titleTv = (TextView) view;
            titleTv.setText(title);
        }
    }

    @Override
    public View initLoadingLayout() {
        return View.inflate(mContext, R.layout.include_loading, null);
    }

    @Override
    public View initNoNetworkLayout() {
        View noNetworkLayout = View.inflate(mContext, R.layout.include_no_network, null);;
        noNetworkLayout.findViewById(R.id.retry_btn).setOnClickListener(this);
        return noNetworkLayout;
    }

    @Override
    public void retry() {
        showLoadingLayout();
    }

    protected void showNoNetworkLayout() {
        removeViewFromRootLayout(mLoadingLayout);
        removeViewFromRootLayout(mContentLayout);
        if (mNoNetworkLayout == null) {
            mNoNetworkLayout = initNoNetworkLayout();
        }
        addViewToRootLayout(mNoNetworkLayout);
    }

    protected void showContentLayout() {
        removeViewFromRootLayout(mLoadingLayout);
        removeViewFromRootLayout(mNoNetworkLayout);
        addViewToRootLayout(mContentLayout);
    }

    protected void showLoadingLayout() {
        removeViewFromRootLayout(mNoNetworkLayout);
        removeViewFromRootLayout(mContentLayout);
        addViewToRootLayout(mLoadingLayout);
    }

    private void addViewToRootLayout(View view) {
        if (view != null && view.getParent() == null) {
            mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            Logger.e("the view is null or it is already have parent");
        }
    }

    private void removeViewFromRootLayout(View view) {
        if (view != null && view.getParent() == mRootLayout) {
            mRootLayout.removeView(view);
        } else {
            Logger.e("the view is null or it's parent is not the root layout");

        }
    }
}

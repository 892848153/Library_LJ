package com.lj.library.activity.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lj.library.R;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;
import com.lj.library.util.UiUtils;


/**
 * @author LJ.Liu
 * @date 2018/4/25.
 */
public abstract class BaseActivity3<T extends ViewDataBinding> extends AppCompatActivity implements BaseViewAction3, View.OnClickListener {

    private static final String TAG = "BaseActivity3";

    protected Activity mContext;

    private View mErrorLayout;

    private View mContentLayout;

    protected T mBinding;

    public static final int STATUS_BAR_HOLDER_ID = R.id.status_bar_holder_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        SampleApplicationLike.getInstance().addActivity(mContext);
        translucentStatusBar();

        mBinding = DataBindingUtil.inflate(getLayoutInflater(), initLayout(savedInstanceState), null, false);
        showContentLayout();
        initComp(savedInstanceState);
    }

    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            addStatusViewWithColor(this, R.color.colorPrimary);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间但不会覆盖状态栏
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 添加状态栏占位视图
     *
     * @param activity
     */
    private void addStatusViewWithColor(Activity activity, int colorId) {
        ViewGroup contentView = UiUtils.getAndroidContentView(mContext);
        View rootView = contentView.getChildAt(0);
        if (rootView != null && rootView.getId() == R.id.status_bar_holder_id) {
            return;
        }

        int statusBarHeight = UiUtils.getStatusBarHeight(this);
        if (rootView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rootView.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
            rootView.setLayoutParams(layoutParams);
        }

        View statusBarView = new View(activity);
        statusBarView.setId(STATUS_BAR_HOLDER_ID);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setBackgroundResource(colorId);
        contentView.addView(statusBarView, 0, lp);
    }

    protected void removeStatusView() {
        ViewGroup contentView = UiUtils.getAndroidContentView(mContext);
        View statusView = contentView.getChildAt(0);
        if (statusView != null && statusView.getId() == STATUS_BAR_HOLDER_ID) {
            contentView.removeViewAt(0);

            View rootView = contentView.getChildAt(0);
            if (rootView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rootView.getLayoutParams();
                layoutParams.topMargin = 0;
                rootView.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * 返回布局的id.
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

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.retry_btn:
                onRetry();
                break;
            default:
        }
    }

    @Override
    public void onRetry() {

    }

    @Override
    public void showContentLayout() {
        if (mContentLayout == null) {
            mContentLayout = mBinding.getRoot();
        }
        showLayout(mContentLayout);
    }

    @Override
    public void showErrorLayout() {
        if (mErrorLayout == null) {
            mErrorLayout = initErrorLayout();
        }
        showLayout(mErrorLayout);
    }

    private View initErrorLayout() {
        View loadingErrorLayout = View.inflate(mContext, R.layout.include_loading_error_layout, null);
        loadingErrorLayout.findViewById(R.id.retry_btn).setOnClickListener(this);
        return loadingErrorLayout;
    }

    private void showLayout(View layout) {
        ViewGroup contentView = UiUtils.getAndroidContentView(mContext);
        View firstChildView = contentView.getChildAt(0);
        if (firstChildView == null) {
            // android.R.id.content中没有内容
            contentView.addView(layout);
        } else if (UiUtils.parentLayoutContains(contentView, layout)) {
            // android.R.id.content中有内容且已经包含了要显示的布局
            Logger.d("此布局已经处于显示状态了，无需再次显示");
        } else {
            // android.R.id.content中有内容且不包含要显示的布局
            if (firstChildView.getId() == STATUS_BAR_HOLDER_ID) {
                // 第一个View是statusBarHolder
                if (contentView.getChildCount() > 1) {
                    // 移除statusBarHolder以外的所有布局
                    contentView.removeViewAt(1);
                }
                // 添加layout布局并且设置其topMargin为statusBar的高度
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                }
                layoutParams.topMargin = UiUtils.getStatusBarHeight(this);
                contentView.addView(layout, layoutParams);
            } else  {
                setContentView(layout);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SampleApplicationLike.getInstance().removeActivity(mContext);
        RxBus.getInstance().unregister(this);
    }
}

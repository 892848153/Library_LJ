package com.lj.library.activity.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.databinding.ActivityBase2Binding;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.FragmentBackManager;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;
import com.lj.library.util.UIUtils;

/**
 * Created by ocean on 2017/8/30.
 */
public abstract class BaseActivity2<T extends ViewDataBinding> extends AppCompatActivity implements BackHandlerInterface, BaseViewAction, View.OnClickListener {

    private static final String TAG = "BaseActivity2";

    protected Activity mContext;

    protected FragmentBackManager mFragmentSelected;

    private View mLoadingLayout;

    private View mLoadingErrorLayout;

    private View mContentLayout;

    private ActivityBase2Binding mBaseBinding;

    protected T mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SampleApplicationLike.getInstance().addActivity(mContext);

        mBaseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base2);

        translucentStatusBar(this);

        View appBar = initAppBar();
        if (appBar != null && appBar.getParent() == null) {
            int height = (int) (48 * getResources().getDisplayMetrics().density);
            mBaseBinding.rootLayout.addView(appBar, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            View back = appBar.findViewById(R.id.back_iv);
            if (back != null) {
                back.setOnClickListener(this);
            }
        }

        mLoadingLayout = initLoadingLayout();
        addViewToRootLayout(mLoadingLayout);

        mBinding = DataBindingUtil.inflate(getLayoutInflater(), initLayout(savedInstanceState), null, false);
        setRealContentView(mBinding.getRoot());
        initComp(savedInstanceState);
    }

    private void translucentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            addStatusViewWithColor(this, R.color.colorPrimary);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间但不会覆盖状态栏
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
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
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View rootView = contentView.getChildAt(0);
        if (rootView != null && rootView.getId() == R.id.status_bar_holder_id) {
            return;
        }

        int statusBarHeight = UIUtils.getStatusBarHeight(this);
        if (rootView != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rootView.getLayoutParams();
            layoutParams.topMargin = statusBarHeight;
            rootView.setLayoutParams(layoutParams);
        }

        View statusBarView = new View(activity);
        statusBarView.setId(R.id.status_bar_holder_id);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarView.setBackgroundResource(colorId);
        contentView.addView(statusBarView, 0, lp);
    }

    protected void removeStatusView(Activity activity) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View statusView = contentView.getChildAt(0);
        if (statusView != null && statusView.getId() == R.id.status_bar_holder_id) {
            contentView.removeViewAt(0);

            View rootView = contentView.getChildAt(0);
            if (rootView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) rootView.getLayoutParams();
                layoutParams.topMargin = 0;
                rootView.setLayoutParams(layoutParams);
            }
        }
    }

//    /**
//     * 根据系统版本调整属性ui，实现Translucent app bar
//     */
//    private void translucentStatusBar() {
//        View parentView = ((ViewGroup) findViewById(Window.ID_ANDROID_CONTENT)).getChildAt(0);
//        View statusBarHolder = mBaseBinding.statusBarHolderView;
//        // 5.0系统开始可以更改状态栏的颜色，所以不需要状态栏底块
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            statusBarHolder.getLayoutParams().height = 0;
//            parentView.setFitsSystemWindows(true);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            // 4.4系统开始支持布局延伸到状态栏底下，不支持更改状态栏颜色，所以需要一个状态栏底块
//            statusBarHolder.getLayoutParams().height = UIUtils.getStatusBarHeight(this);
//            parentView.setFitsSystemWindows(false);
//        } else {
//            statusBarHolder.getLayoutParams().height = 0;
//        }
//    }

    public void setRealContentView(int layoutId) {
        setRealContentView(View.inflate(this, layoutId, null));
    }

    public void setRealContentView(View view) {
        mContentLayout = view;
        mBaseBinding.rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setSelectedFragment(FragmentBackManager selectedFragment) {
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
        View view = mBaseBinding.rootLayout.findViewById(R.id.title_tv);
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
    public View initLoadingErrorLayout() {
        View loadingErrorLayout = View.inflate(mContext, R.layout.include_loading_error_layout, null);
        loadingErrorLayout.findViewById(R.id.retry_btn).setOnClickListener(this);
        return loadingErrorLayout;
    }

    protected void setLoadingErrorIcon(@DrawableRes int drawId) {
        if (mLoadingErrorLayout == null) {
            throw new IllegalStateException("loading error layout has never initialed, please invoke showLoadingErrorLayout() method first.");
        }

        ImageView iconIv = (ImageView) mLoadingErrorLayout.findViewById(R.id.error_ic_iv);
        iconIv.setImageResource(drawId);
    }

    protected void setLoadingErrorContent(@StringRes int strId) {
        if (mLoadingErrorLayout == null) {
            throw new IllegalStateException("loading error layout has never initialed, please invoke showLoadingErrorLayout() method first.");
        }

        TextView promptTv = (TextView) mLoadingErrorLayout.findViewById(R.id.prompt_tv);
        promptTv.setText(strId);
    }

    @Override
    public void retry() {
        showLoadingLayout();
    }

    @Override
    public void showLoadingLayout() {
        removeViewFromRootLayout(mLoadingErrorLayout);
        removeViewFromRootLayout(mContentLayout);
        addViewToRootLayout(mLoadingLayout);
    }

    @Override
    public void showContentLayout() {
        removeViewFromRootLayout(mLoadingLayout);
        removeViewFromRootLayout(mLoadingErrorLayout);
        addViewToRootLayout(mContentLayout);
    }

    @Override
    public void showLoadingErrorLayout() {
        removeViewFromRootLayout(mLoadingLayout);
        removeViewFromRootLayout(mContentLayout);
        if (mLoadingErrorLayout == null) {
            mLoadingErrorLayout = initLoadingErrorLayout();
        }
        addViewToRootLayout(mLoadingErrorLayout);
    }

    private void addViewToRootLayout(View view) {
        if (view != null && view.getParent() == null) {
            mBaseBinding.rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            Logger.e("the view is null or it is already have parent");
        }
    }

    private void removeViewFromRootLayout(View view) {
        if (view != null && view.getParent() == mBaseBinding.rootLayout) {
            mBaseBinding.rootLayout.removeView(view);
        } else {
            Logger.e("the view is null or it's parent is not the root layout");

        }
    }
}
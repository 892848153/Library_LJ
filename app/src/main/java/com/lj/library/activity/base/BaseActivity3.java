package com.lj.library.activity.base;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lj.library.R;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;
import com.lj.library.util.UiUtils;


/**
 * @author LJ.Liu
 * @date 2018/4/25.
 */
public abstract class BaseActivity3<T extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity implements BaseViewAction3, View.OnClickListener {

    private static final String TAG = "BaseActivity3";

    protected Activity mContext;

    protected T mBinding;

    private VM mViewModel;

    private View mErrorLayout;

    private View mContentLayout;

    private static final int STATUS_BAR_HOLDER_ID = R.id.status_bar_holder_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        overflowStatusBar();

        mBinding = DataBindingUtil.inflate(getLayoutInflater(), initLayout(savedInstanceState), null, false);
        mBinding.setVariable(initVariableId(), mViewModel = initViewModel());

        showContentLayout();

        initComp(savedInstanceState);

        mViewModel.onCreate();

        mViewModel.onRegisterRxBus();
    }

    /**
     * 点击左上角返回按钮
     *
     * @param view
     */
    public void onBack(View view) {
        finish();
    }

    protected void overflowStatusBar() {
        overflowStatusBar(R.color.colorPrimary);
    }

    /**
     * Android 4.4采用半透明标题栏方式把布局顶到状态栏底下，5.0开始不用设置半透明状态栏也可以把布局顶到状态栏底下.
     * 不管是采用半透明状态栏的方式还是别的方式，只要把布局顶到状态栏底下，那么android:windowSoftInputMode="adjustResize"
     * 将失效。只有在根布局设置android:fitsSystemWindows="true"才可以让上面的属性生效
     * 不过设置了android:fitsSystemWindows="true"那么布局又不能顶到状态栏底下了，这是个矛盾.
     */
    protected void overflowStatusBar(@ColorRes int statusBarBgColorRes) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        addStatusBarHolderWithColor(this, statusBarBgColorRes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0开始可以采用这段代码把布局顶到状态栏底下。
            Window window = getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | decorView.getSystemUiVisibility();
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            // 4.4系统只能采用"半透明状态栏"的方式把布局顶到状态栏底下
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(attributes);
        }
    }

    /**
     * 添加状态栏占位视图
     *
     * @param activity
     */
    private void addStatusBarHolderWithColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        removeStatusBarHolderIfExist();

        ViewGroup contentView = UiUtils.getAndroidContentView(mContext);
        View rootView = contentView.getChildAt(0);
        int statusBarHeight = UiUtils.getStatusBarHeight(this);

        setViewTopMargin(rootView, statusBarHeight);

        View statusBarHolder = new View(activity);
        statusBarHolder.setId(STATUS_BAR_HOLDER_ID);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarHolder.setBackgroundResource(colorId);
        contentView.addView(statusBarHolder, 0, lp);
    }

    /**
     * 图片需要顶上状态栏时使用此方法.
     */
    protected void removeStatusBarHolderIfExist() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ViewGroup contentView = UiUtils.getAndroidContentView(mContext);
        View statusBarHolder = contentView.getChildAt(0);
        if (statusBarHolder != null && statusBarHolder.getId() == STATUS_BAR_HOLDER_ID) {
            contentView.removeViewAt(0);

            View rootView = contentView.getChildAt(0);
            setViewTopMargin(rootView, 0);
        }
    }

    private void setViewTopMargin(final View view, final int topMargin) {
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = topMargin;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 状态栏亮色模式，状态栏字体颜色为黑色.
     */
    protected void lightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(false);
        }
    }

    /**
     * 状态栏暗色模式，状态栏字体颜色为白色.
     */
    protected void darkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(true);
        }
    }

    /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private final void setSystemBarTheme(final boolean isDark) {
        // Fetch the current flags.
        final int curFlags = getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        getWindow().getDecorView().setSystemUiVisibility(isDark ? (curFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (curFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));

    }

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    protected abstract int initVariableId();

    /**
     * 实例化ViewModel
     * @return
     */
    protected abstract VM initViewModel();

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
        RxBus.getInstance().unregister(this);
        mViewModel.onRemoveRxBus();
        mViewModel.onDestroy();
    }
}

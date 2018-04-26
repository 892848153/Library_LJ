package com.lj.library.activity.base;

import android.app.Activity;
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
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.FragmentBackManager;
import com.lj.library.util.RxBus;
import com.lj.library.util.UiUtils;

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
            translucentStatusBar(this);
            ButterKnife.bind(this);
            initComp(savedInstanceState);
        }
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

        int statusBarHeight = UiUtils.getStatusBarHeight(this);
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

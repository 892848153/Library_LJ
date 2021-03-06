package com.lj.library.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.lj.library.R;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.FragmentBackManager;
import com.lj.library.util.Logger;
import com.lj.library.util.RxBus;
import com.lj.library.util.StatusBarUtils;
import com.lj.library.util.UiUtils;


/**
 * @author LJ.Liu
 * @date 2018/4/25.
 */
public abstract class BaseActivity4<T extends ViewDataBinding, VM extends BaseViewModel> extends AppCompatActivity
        implements BaseViewAction3, BackHandlerInterface, View.OnClickListener {

    private static final String TAG = "BaseActivity4";

    protected Activity mContext;

    protected T mBinding;

    protected VM mViewModel;

    protected FragmentBackManager mFragmentSelected;

    private View mErrorLayout;

    private View mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        mViewModel = initViewModel();

//        enableContentTransition();

        mBinding = DataBindingUtil.inflate(getLayoutInflater(), initLayout(savedInstanceState), null, false);

        showContentLayout();

//        initContentTransition();

        StatusBarUtils.overflowStatusBar(mContext, true);

        initComp(savedInstanceState);

        if (mViewModel != null) {
            mViewModel.onCreate();
            mViewModel.onRegisterRxBus();
        }
    }

    private void enableContentTransition() {
        // 这行效果跟在app主题中<item name="android:windowContentTransitions">true</item> 一样
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    }

    /**
     * To get the full effect of a transition, you must enable window content transitions on both the calling and called activities.
     * Otherwise, the calling activity will start the exit transition, but then you'll see a window transition (like scale or fade).
     *
     * To start an enter transition as soon as possible, use the Window.setAllowEnterTransitionOverlap() function on the called activity.
     * This lets you have more dramatic enter transitions.
     */
    protected void initContentTransition() {
        // 下面这些方法必须在setContentView方法后才能设置，否则Transition框架无法找到界面视图
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());
        getWindow().setReenterTransition(new Explode());
        getWindow().setReturnTransition(new Explode());
//        getWindow().setAllowEnterTransitionOverlap();
//        getWindow().setAllowReturnTransitionOverlap();

        Transition sharedTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform);
        getWindow().setSharedElementEnterTransition(sharedTransition);
        getWindow().setSharedElementExitTransition(sharedTransition);
        getWindow().setSharedElementReenterTransition(sharedTransition);
        getWindow().setSharedElementReturnTransition(sharedTransition);
//        getWindow().setSharedElementsUseOverlay();
    }

    public void startActivityUsingTransition(Intent intent) {
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    public void startActivityWithSharedElements(Intent intent, Pair<View, String>... sharedElements) {
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElements).toBundle());
    }

    public void startActivityWithSharedElementsFromFragment(Fragment fragment, Intent intent, Pair<View, String>... sharedElements) {
        startActivityFromFragment(fragment, intent, 1, ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElements).toBundle());
    }

    /**
     * 点击左上角返回按钮
     *
     * @param view
     */
    public void onBack(View view) {
        finish();
    }

    /**
     * 实例化ViewModel
     *
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
    public void onBackPressed() {
        if (mFragmentSelected == null || !mFragmentSelected.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void setSelectedFragment(FragmentBackManager selectedFragment) {
        mFragmentSelected = selectedFragment;
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
        ViewGroup androidContentView = UiUtils.getAndroidContentView(mContext);
//        View firstChildView = androidContentView.getChildAt(0);
        /*if (firstChildView == null) {
            // android.R.id.content中没有内容
            androidContentView.addView(layout);
        } else */if (UiUtils.parentLayoutContains(androidContentView, layout)) {
            // android.R.id.content中有内容且已经包含了要显示的布局
            Logger.d("此布局已经处于显示状态了，无需再次显示");
        } else {
//            // android.R.id.content中有内容且不包含要显示的布局
//            if (firstChildView.getId() == STATUS_BAR_HOLDER_ID) {
//                // 第一个View是statusBarHolder
//                if (androidContentView.getChildCount() > 1) {
//                    // 移除statusBarHolder以外的所有布局
//                    androidContentView.removeViewAt(1);
//                }
//                // 添加layout布局并且设置其topMargin为statusBar的高度
//                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
//                if (layoutParams == null) {
//                    layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                }
//                layoutParams.topMargin = UiUtils.getStatusBarHeight(this);
//                androidContentView.addView(layout, layoutParams);
//            } else  {
                setContentView(layout);
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister(this);
        if (mViewModel != null) {
            mViewModel.onRemoveRxBus();
            mViewModel.onDestroy();
        }
    }
}

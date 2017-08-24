package com.lj.library.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lj.library.R;
import com.lj.library.activity.base.BaseViewAction;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.util.Logger;

import butterknife.ButterKnife;

/**
 * Created by ocean on 2017/8/21.
 */
public abstract class BaseFragment1 extends Fragment implements FragmentBackManager, BaseViewAction, View.OnClickListener {

    private static final String TAG = "BaseFragment1";

    /**
     * 解决 {@link #getActivity()} 为null的bug
     **/
    protected Activity mActivity;

    protected View mRootView;

    private Fragment mContent;

    private BackHandlerInterface mBackHandlerInterface;

    private LinearLayout mRootLayout;

    private View mLoadingLayout;

    private View mLoadingErrorLayout;

    private View mContentLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(mActivity instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.mBackHandlerInterface = (BackHandlerInterface) mActivity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != mRootView) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        } else {
            mRootView = inflater.inflate(R.layout.activity_base, container, false);
            mRootLayout = (LinearLayout) mRootView.findViewById(R.id.root_layout);

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
            ButterKnife.bind(this, mRootView);
            initComp(savedInstanceState);
        }
        return mRootView;
    }

    public void setContentView(int layoutId) {
        setContentView(View.inflate(mActivity, layoutId, null));
    }

    public void setContentView(View view) {
        mContentLayout = view;
        mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandlerInterface.setSelectedFragment(this);
    }

    /**
     * 第一种跳转页面的方法,按返回键会从onCreateView(LayoutInflater, ViewGroup, Bundle)
     * 开始执行生命周期, 不会执行onCreateView(LayoutInflater)方法.
     *
     * @param targetFragment
     */
    public void startFragment(Fragment targetFragment) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, targetFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 第二种跳转页面的方法,按返回键弹出栈,只会调用Fragment to的onHiddenChanged方法.
     *
     * @param from
     * @param to
     */
    protected void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // 先判断是否被add过
            if (!to.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(from).add(R.id.fragment_container, to).addToBackStack(null).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(from).show(to).addToBackStack(null).commit();
            }
        }
    }

    public void finish() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();
    }

    protected void clearBackStack() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        while (manager.getBackStackEntryCount() > 0) {
            manager.popBackStackImmediate();
        }
    }

    /**
     * 给Fragment设置主题,主题传递0，则采用系统的默认主题.
     * <p/>
     * <pre>
     * public View onCreateView(LayoutInflater inflater, ViewGroup container,
     * 		Bundle savedInstanceState) {
     * 	inflater = setTheme(inflater);
     * 	View view = inflater.inflate(R.layout.item_flagment_info, container, false);
     * 	return view;
     * }
     * </pre>
     *
     * @param inflater
     * @return
     */
    protected LayoutInflater setTheme(LayoutInflater inflater) {
        // 给Fragment设置主题,主题传递0，则采用系统的默认主题
        final Context contextThemeWrapper = new ContextThemeWrapper(mActivity, 0);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //        MyApplication.getRefWatcher().watch(this);
        SampleApplicationLike.getRefWatcher().watch(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

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
        return View.inflate(mActivity, R.layout.include_normal_header, null);
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
        return View.inflate(mActivity, R.layout.include_loading, null);
    }

    @Override
    public View initLoadingErrorLayout() {
        View loadingErrorLayout = View.inflate(mActivity, R.layout.include_loading_error_layout, null);
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

    /**
     * 子类在次方法中返回布局并且初始化.
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

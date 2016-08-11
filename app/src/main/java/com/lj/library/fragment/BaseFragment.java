package com.lj.library.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lj.library.R;
import com.lj.library.application.MyApplication;
import com.lj.library.http.apache.HttpHelper;
import com.lj.library.util.LogUtil;
import com.lj.library.util.RxBus;
import com.lj.library.util.Toaster;
import com.lj.library.widget.LoadingProgress;

import butterknife.ButterKnife;

/**
 * Fragment基础类.
 *
 * @author jie.liu
 * @time 2014年10月28日 上午10:40:13
 */
public abstract class BaseFragment extends Fragment implements HttpHelper.OnHttpCallback {

    /**
     * 解决 {@link #getActivity()} 为null的bug
     **/
    protected Activity mActivity;

    protected View mRootView;

    private Fragment mContent;

    private BackHandlerInterface mBackHandlerInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!(mActivity instanceof BackHandlerInterface)){
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }else{
            this.mBackHandlerInterface = (BackHandlerInterface)mActivity;
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
            mRootView = inflater.inflate(initLayout(savedInstanceState), container, false);
            ButterKnife.bind(this, mRootView);
            initComp(savedInstanceState);
        }
        return mRootView;
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
    public void startFragment(BaseFragment targetFragment) {
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
    public void onHttpNetworkNotFound(String path) {
        Toaster.showShort(mActivity, "未检测到可用的网络");
        dismissDialogIfNeeded();
    }

    @Override
    public void onHttpReturn(String path, int response, String result) {

    }

    @Override
    public void onHttpError(String path, Exception exception) {
        Toaster.showShort(mActivity, "网络连接异常，请重试");
        dismissDialogIfNeeded();
    }

    @Override
    public void onHttpError(String path, int response) {
        Toaster.showShort(mActivity, "网络连接超时，请重试");
        dismissDialogIfNeeded();
    }

    @Override
    public void onHttpSuccess(String path, String result) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求路径：").append(path).append("\n").append(result);
        LogUtil.d(this, sb.toString());
        dismissDialogIfNeeded();
    }

    @Override
    public void onHttpNothingReturn(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("请求路径：").append(path).append("\n").append("什么也没有返回");
        LogUtil.d(this, sb.toString());
        dismissDialogIfNeeded();
    }

    private void dismissDialogIfNeeded() {
        // 如果有启动进度条对话框将被关闭
        LoadingProgress.getInstance().dismiss();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher().watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RxBus.getInstance().unregister(this);
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

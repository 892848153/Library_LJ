package com.lj.library.activity.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.lj.library.R;
import com.lj.library.application.SampleApplicationLike;
import com.lj.library.fragment.BackHandlerInterface;
import com.lj.library.fragment.BaseFragment;
import com.lj.library.util.LogUtil;
import com.lj.library.util.RxBus;
import com.lj.library.util.UIUtils;

/**
 * 基础类.
 *
 * @author jie.liu
 * @time 2015年3月6日 上午10:55:47
 */
public abstract class BaseActivity1 extends AppCompatActivity implements BackHandlerInterface {

    private static final String TAG = "BaseActivity1";

    protected Activity mContext;

    protected BaseFragment mFragmentSelected;

    private LinearLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SampleApplicationLike.getInstance().addActivity(mContext);

        setContentView(R.layout.activity_base);
        translucentStatusBar();
        setBodyView(initLayout(savedInstanceState));
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

    public void setBodyView(int layoutId) {
        setBodyView(View.inflate(this, layoutId, null));
    }

    public void setBodyView(View view) {
        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (mRootLayout == null) return;
        LogUtil.i(this, mRootLayout.getChildCount() + "");
        mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initAppbar();
    }

    protected void initAppbar() {

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

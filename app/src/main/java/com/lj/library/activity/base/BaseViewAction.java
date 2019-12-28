package com.lj.library.activity.base;

import androidx.annotation.StringRes;
import android.view.View;

/**
 * Created by ocean on 2017/7/14.
 */
public interface BaseViewAction {

    /**
     * 初始化顶部标题栏，如果不想使用默认的标题栏则需要复写这个方法，
     * 如果不想要标题栏则返回null.
     * @return
     */
    View initAppBar();

    /**
     * 如果没有复写{@link #initAppBar()}方法，即使用的是默认的标题栏，
     * 则调用此方法可以更改标题栏的标题.
     * @param titleRes
     */
    void setTitle(@StringRes int titleRes);

    /**
     * 如果没有复写{@link #initAppBar()}方法，即使用的是默认的标题栏，
     * 则调用此方法可以更改标题栏的标题.
     * @param title
     */
    void setTitle(String title);

    /**
     * 初始化加载数据时显示的页面，如果不想使用默认的加载页面则需要复写这个方法，
     * 如果不想要加载页面则返回null.
     * @return
     */
    View initLoadingLayout();

    /**
     * 初始化加载出错时显示的页面，如果不想使用默认的加载出错页面则需要复写这个方法，
     * 如果不想要加载出错页面则返回null.
     * @return
     */
    View initLoadingErrorLayout();

    /**
     * 如果没有复写{@link #initLoadingErrorLayout()}方法，即使用的是默认的加载出错页面，
     * 则点击加载出错页面的按钮会调用此方法.
     */
    void retry();

    /**
     * 显示Loading布局.
     */
    void showLoadingLayout();

    /**
     * 显示实际需要显示的布局.
     */
    void showContentLayout();

    /**
     * 显示加载出错的布局.
     */
    void showLoadingErrorLayout();

}

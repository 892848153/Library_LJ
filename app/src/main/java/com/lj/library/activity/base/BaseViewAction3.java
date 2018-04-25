package com.lj.library.activity.base;

/**
 * @author LJ.Liu
 * @date 2018/4/25.
 */
public interface BaseViewAction3 {

    /**
     * 显示特定的错误页面.
     */
    void showErrorLayout();

    /**
     * 显示页面原本的布局内容.
     */
    void showContentLayout();

    /**
     * 错误页面的retry按钮被点击的回调.
     */
    void onRetry();


}

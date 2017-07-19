package com.lj.library.activity.base;

import android.support.annotation.StringRes;
import android.view.View;

/**
 * Created by ocean on 2017/7/14.
 */
public interface BaseViewAction {

    View initAppBar();

    void setTitle(@StringRes int titleRes);

    void setTitle(String title);

    View initLoadingLayout();

    View initNoNetworkLayout();

    void retry();

}

package com.lj.library.activity.base;

import android.app.Activity;

/**
 * @author LJ.Liu
 * @date 2018/5/2.
 */
public abstract class BaseViewModel implements IBaseViewModel {

    protected Activity mContext;

    public String text;

    public BaseViewModel(final Activity context) {
        mContext = context;
    }
}

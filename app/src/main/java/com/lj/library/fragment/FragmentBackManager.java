package com.lj.library.fragment;

/**
 * Created by ocean on 2017/8/21.
 */
public interface FragmentBackManager {

    /**
     * 按硬件返回健会调用此方法.
     *
     * @return true:消耗掉返回健事件, false: 不消耗事件，事件继续向Activity传递
     */
    boolean onBackPressed();


}

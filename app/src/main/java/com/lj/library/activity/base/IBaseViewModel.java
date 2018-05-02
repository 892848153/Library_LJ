package com.lj.library.activity.base;

/**
 * @author LJ.Liu
 * @date 2018/5/2.
 */
public interface IBaseViewModel {

    /**
     * View的界面创建时回调
     */
    void onCreate();

    /**
     * View的界面销毁时回调
     */
    void onDestroy();

    /**
     * 注册RxBus
     */
    void onRegisterRxBus();
    /**
     * 移除RxBus
     */
    void onRemoveRxBus();
}

package com.lj.library.bean;

import android.app.Activity;

import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 15/9/3.
 */
public class Menu {

    public BaseFragment targetFragment;

    public Class<? extends Activity> targetActivity;

    public String describe;

    public Menu() {
    }

    public Menu(Class<? extends Activity> targetActivity, String describe) {
        this.targetActivity = targetActivity;
        this.describe = describe;
    }

    public Menu(BaseFragment targetFragment, String describe) {
        this.targetFragment = targetFragment;
        this.describe = describe;
    }
}

package com.lj.library.bean;

import com.lj.library.fragment.BaseFragment;

/**
 * Created by liujie_gyh on 15/9/3.
 */
public class Menu {

    public BaseFragment targetFragment;

    public String describe;

    public Menu() {
    }

    public Menu(BaseFragment targetFragment, String describe) {
        this.targetFragment = targetFragment;
        this.describe = describe;
    }
}

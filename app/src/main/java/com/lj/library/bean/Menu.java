package com.lj.library.bean;

/**
 * Created by liujie_gyh on 15/9/3.
 */
public class Menu {

    public Class<?> target;

    public String describe;

    public Menu() {
    }

    public Menu(Class<?> target, String describe) {
        this.target = target;
        this.describe = describe;
    }
}

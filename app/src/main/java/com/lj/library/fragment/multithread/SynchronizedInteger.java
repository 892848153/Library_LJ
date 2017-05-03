package com.lj.library.fragment.multithread;

/**
 * {@link #set(int)}方法对value的修改虽然是原子操作，
 * 不过没办法保证可见性,即没办法保证别的线程能读取到
 * 当前线程对{@link #value}属性修改后的值。<p/>
 * 所以需要使用同步来保证其可见性,或者使用volatile关键字来保证可见性。<br/>
 * Created by liujie_gyh on 2017/5/4.
 */
public class SynchronizedInteger {
//    private volatile  int value;
    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int value) {
        this.value = value;
    }
}

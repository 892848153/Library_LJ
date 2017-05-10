package com.lj.library.fragment.multithread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 虽然两个状态变量lower和upper是线程安全的，但是他们之间存在着下届和上届约束的不变性条件。
 * {@link #setLower(int)}和{@link #setUpper(int)}两个方法必须是原子操作的才能
 * 确保不变性条件，所以需要同步.
 * Created by liujie_gyh on 2017/5/4.
 */
public class NumberRange {

    private AtomicInteger lower = new AtomicInteger(0);
    private AtomicInteger upper = new AtomicInteger(0);

    public synchronized void setLower(int i) {
        if (i > upper.get()) {
            throw new IllegalArgumentException("can't set lower to " + i + " > upper");
        }
        lower.set(i);
    }

    public synchronized void setUpper(int i) {
        if (i < lower.get()) {
            throw new IllegalArgumentException("can't set upper to " + i + " < lower");
        }
        upper.set(i);
    }

    public synchronized boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}

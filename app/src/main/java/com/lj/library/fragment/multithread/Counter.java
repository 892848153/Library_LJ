package com.lj.library.fragment.multithread;

import javax.annotation.concurrent.GuardedBy;

/**
 * ++value属于"读取-修改-写入"的复合操作，在这里需要其以原子操作来执行
 * 所以需要同步。<p/>或者不用同步改用{@link java.util.concurrent.atomic.AtomicLong}
 * 来保证其原子性<br/>
 * Created by liujie_gyh on 2017/5/4.
 */
public class Counter {

    @GuardedBy("this")
    private long value = 0;

    public synchronized long getValue() {
        return value;
    }

    public synchronized long increment() {
        if (value == Long.MAX_VALUE)
            throw new IllegalStateException("counter overflow");
        return ++value;
    }
}

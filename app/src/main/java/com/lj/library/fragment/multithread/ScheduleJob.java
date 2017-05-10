package com.lj.library.fragment.multithread;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import bolts.Task;

/**
 * 用DelayQueue实现ScheduleThreadPool的功能.
 *
 * Created by liujie_gyh on 2017/5/10.
 */
public class ScheduleJob {

    private Executor mExecutor = Executors.newFixedThreadPool(5);

    private DelayQueue<DelayedElement> mDelayQueue = new DelayQueue<>();

    private ScheduleJob(){}

    private static class LazyHolder {
        private static ScheduleJob INSTANCE = new ScheduleJob();
    }

    public static ScheduleJob getInstance() {
        return LazyHolder.INSTANCE;
    }
    /**
     * 初始化守护线程
     */
    public void init() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                execute();
            }
        });
        thread.setDaemon(true);
        thread.setName("Task Queue Daemon Thread");
        thread.start();
    }

    private void execute() {
        System.out.println("start:" + System.currentTimeMillis());
        while (true) {
            try {
                //从延迟队列中取值,如果没有对象过期则队列一直等待，
                DelayedElement t1 = mDelayQueue.take();
                if (t1 != null) {
                    Runnable task = t1.getTask();
                    if (task == null) {
                        continue;
                    }
                    mExecutor.execute(task);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void put(long timeInMillis, Runnable task) {
        //创建一个任务
        DelayedElement k = new DelayedElement(timeInMillis, task);
        //将任务放在延迟的队列中
        mDelayQueue.put(k);
    }

    public boolean endTask(Task<Runnable> task){
        return mDelayQueue.remove(task);
    }

    private static class DelayedElement<T extends Runnable> implements Delayed {

        private final long mExpire;

        private final T mTask;

        public DelayedElement(long delayInMillis) {
            this(delayInMillis, null);
        }

        public DelayedElement(long delayInMillis, T task) {
            if (delayInMillis < 0)
                delayInMillis = 0;
            mExpire = Calendar.getInstance().getTimeInMillis() + delayInMillis;
            mTask = task;
        }

        public T getTask() {
            return mTask;
        }

        /**
         * 还需要多久才逾期。取出元素时会调用此方法来判断该元素的触发时间是否逾期.
         *
         * @param unit
         * @return
         */
        @Override
        public long getDelay(@NonNull TimeUnit unit) {
            return unit.convert(mExpire - Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * {@link java.util.concurrent.DelayQueue}中的元素将按照这个方法来排序.
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(@NonNull Delayed o) {
            return (int) (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}

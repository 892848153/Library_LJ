package com.lj.library.fragment.multithread;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lock的一些使用方法.
 * Created by liujie_gyh on 2017/5/12.
 */
public class LockDemo {

    private final ReentrantLock mReentrantLock = new ReentrantLock();

    private final ReentrantReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();


    public static final void main(String... args) {
        final LockDemo lockDemo = new LockDemo();

        //测试lock
        new Thread() {
            @Override
            public void run() {
                super.run();
                lockDemo.lock(Thread.currentThread());
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                super.run();
                lockDemo.lock(Thread.currentThread());
            }
        }.start();

//        //测试tryLock
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.tryLock(Thread.currentThread());
//            }
//        }.start();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.tryLock(Thread.currentThread());
//            }
//        }.start();

//        //测试lockInterruptibly
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.lockInterruptibly(Thread.currentThread());
//            }
//        }.start();
//        Thread t2 = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.lockInterruptibly(Thread.currentThread());
//            }
//        };
//        t2.start();
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        t2.interrupt();


//        //测试读写锁
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.readLock(Thread.currentThread());
//            }
//        }.start();
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                lockDemo.writeLock(Thread.currentThread());
//            }
//        }.start();
    }

    private void lock(Thread thread) {
        mReentrantLock.lock();
        try {
            System.out.println(thread.getName() + "lock获得了锁");
            for (int i = 0; i < 20; i++) {
                System.out.println("lock获得锁:" + i);
            }
        } finally {
            System.out.println(thread.getName() + "lock释放了锁");
            mReentrantLock.unlock();
        }
    }

    private void lockInterruptibly(Thread thread) {
        try {
            mReentrantLock.lockInterruptibly();
            try {
                System.out.println(thread.getName() + "lockInterruptibly获得了锁");
                long startTime = System.currentTimeMillis();
                for(;;) {
                    if(System.currentTimeMillis() - startTime >= 3000)
                        break;
                }
            } finally {
                System.out.println(thread.getName() + "lockInterruptibly释放了锁");
                mReentrantLock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(thread.getName() + "lockInterruptibly被中断");
        }
    }

    /**
     * tryLock不会阻塞
     * @param thread
     */
    private void tryLock(Thread thread) {
        if (mReentrantLock.tryLock()) {
            try {
                System.out.println(thread.getName() + "tryLock获得了锁");
                for (int i = 0; i < 20; i++) {
                    System.out.println("tryLock获得锁:" + i);
                }
            } finally {
                System.out.println(thread.getName() + "tryLock释放了锁");
                mReentrantLock.unlock();
            }
        } else {
            System.out.println(thread.getName() + "tryLock获取锁失败");
        }
    }

    private void readLock(Thread thread) {
        mReadWriteLock.readLock().lock();
        try {
            System.out.println(thread.getName() + "readLock获得了读锁");
            for (int i = 0; i < 20; i++) {
                System.out.println("读取" + i);
            }
        } finally {
            System.out.println(thread.getName() + "readLock释放了读锁");
            mReadWriteLock.readLock().unlock();
        }
    }

    private void writeLock(Thread thread) {
        mReadWriteLock.writeLock().lock();
        try {
            System.out.println(thread.getName() + "writeLock获得了写锁");
            for (int i = 0; i < 10; i++) {
                System.out.println("写入" + i);
            }
        } finally {
            System.out.println(thread.getName() + "writeLock释放了写锁");
            mReadWriteLock.writeLock().unlock();
        }
    }
}

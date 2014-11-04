package com.lj.library.asyntask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;

/**
 * 线程池，供 {@link AsyncTask#executeOnExecutor(Executor, Object...)} 调用.
 * 
 * 
 * @time 2014年11月4日 下午2:30:24
 * @author jie.liu
 */
public class ExecutorHolder {

	private static final int CORE_POOL_SIZE = 5;
	private static final int MAXIMUM_POOL_SIZE = 128;
	private static final int EKEEP_ALIVE = 10;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(
			128);

	public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, EKEEP_ALIVE,
			TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

}

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
 * 智能task，根据网络状况，决定是用3.0之后的串行执行线程的方案，还是用3.0之前的并行执行线程的方案.
 * <p/>
 * TODO 未完
 * 
 * @time 2014年11月4日 下午2:30:24
 * @author jie.liu
 */
public abstract class AsynTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {

	private static final int ELDER_CORE_POOL_SIZE = 5;
	private static final int ELDER_MAXIMUM_POOL_SIZE = 128;
	private static final int ELDER_KEEP_ALIVE = 10;

	private static final ThreadFactory sElderThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};

	private static final BlockingQueue<Runnable> sElderPoolWorkQueue = new LinkedBlockingQueue<Runnable>(
			128);

	public static final Executor ELDER_ELDER_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(
			ELDER_CORE_POOL_SIZE, ELDER_MAXIMUM_POOL_SIZE, ELDER_KEEP_ALIVE,
			TimeUnit.SECONDS, sElderPoolWorkQueue, sElderThreadFactory);

	public static void execute(Runnable runnable) {
	}
}

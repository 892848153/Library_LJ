package com.lj.library.application;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 异常处理.
 * 
 * @time 2014-6-3 下午4:04:56
 * @author jie.liu
 */

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO 写入Log，上传到服务器,关闭程序

	}
}

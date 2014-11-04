package com.lj.library.dao;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 数据库执行类， 里面开启线程操作.
 * 
 * @time 2014年11月3日 下午3:03:27
 * @author jie.liu
 */
public class DBTaskRunner {

	private final DBManager mManager;

	private DBCallback mCallback;

	public DBTaskRunner(Context context) {
		mManager = new DBManager(context);
	}

	public void setOnDBCallback(DBCallback dbCallback) {
		if (mCallback != dbCallback) {
			mCallback = dbCallback;
		}
	}

	/**
	 * 查询下载进度.
	 * 
	 * @param requestCode
	 * @param path
	 */
	public void queryDownloadProgress(int requestCode, final String path) {
		Task task = new Task() {

			@Override
			public String execute() {
				return mManager.queryDownloadProgress(path);
			}
		};

		read(task, requestCode);
	}

	private void read(Task task, int requestCode) {
		doIt(task, false, requestCode);
	}

	private void write(Task task, int requestCode) {
		doIt(task, true, requestCode);
	}

	private void doIt(Task task, boolean writable, int requestCode) {
		new DBTask(task, writable, requestCode).execute();
	}

	private class DBTask extends AsyncTask<String, Void, String> {

		private final Task mTask;

		private final boolean mWritable;

		private final int mRequestCode;

		public DBTask(Task task, boolean writable, int requestCode) {
			mTask = task;
			mWritable = writable;
			mRequestCode = requestCode;
		}

		@Override
		protected String doInBackground(String... params) {
			if (mTask != null) {
				return mTask.execute();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mCallback != null) {
				if (mWritable) {
					mCallback.onWriteReturned(mRequestCode);
				} else {
					mCallback.onReadReturned(mRequestCode, result);
				}
			}

		}
	}

	public static interface DBCallback {

		/**
		 * 写操作返回,运行在主线程.
		 * 
		 * @param requestCode
		 *            请求码
		 */
		void onWriteReturned(int requestCode);

		/**
		 * 读操作返回， 运行在主线程.
		 * 
		 * @param requestCode
		 *            请求码
		 * @param result
		 *            读结果，以Json格式的字符串返回
		 */
		void onReadReturned(int requestCode, String result);

	}

	private interface Task {
		String execute();
	}
}

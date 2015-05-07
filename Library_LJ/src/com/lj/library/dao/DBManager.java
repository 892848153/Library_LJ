package com.lj.library.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.lj.library.bean.DownloadProgress;
import com.lj.library.dao.table.MultiDownload;
import com.lj.library.util.JsonUtil;
import com.lj.library.util.LogUtil;

/**
 * getReadableDatabase先以读写方式打开数据库，如果数据库的磁盘空间满了，就会打开失败，当打开失败后会继续尝试以只读方式打开数据库.<br/>
 * 在多线程中，如果第一个线程先调用getWritableDatabase，后面线程再次调用，或者第一个线程先调用getReadableDatabase，<br/>
 * 后面的线程调用getWritableDatabase ，那么后面的这个方法是会失败的，因为数据库文件打开后会加锁，必须等前面的关闭后后面的调用才能正常执行<br/>
 * 正是因为这个原因，可以1 Write+Many Read
 * (有可能产生冲突，因为第一个getReadableDatabase有可能先于getWritableDatabase执行，导致后面的失败)， 也可以Many
 * Read，但是不可能Many Write。所以使用单例加上同步的数据库操作方法，就不会出现死锁的问题.<br/>
 * SQLiteDataBase 在API 11 多了一个 属性 ENABLE_WRITE_AHEAD_LOGGING。
 * 可以打，enableWriteAheadLogging()，可以关闭disableWriteAheadLogging()，默认是关闭的。
 * 
 * @time 2014年10月10日 下午5:32:22
 * @author jie.liu
 */
@SuppressLint("NewApi")
public class DBManager {

	private final DBHelper mDbHelper;

	public DBManager(Context context) {
		mDbHelper = DBHelper.getInstance(context);
		if (Build.VERSION.SDK_INT >= 11) {
			// 参考api文档，这个属性关闭时，不允许读，写同时进行，通过 锁 来保证。
			// 当打开时，它允许一个写线程与多个读线程同时在一个SQLiteDatabase上起作用。
			// 实现原理是写操作其实是在一个单独的文件，不是原数据库文件。
			// 所以写在执行时，不会影响读操作，读操作读的是原数据文件，是写操作开始之前的内容。
			// 在写操作执行成功后，会把修改合并会原数据库文件。此时读操作才能读到修改后的内容。但是这样将花费更多的内存。
			mDbHelper.getWritableDatabase().enableWriteAheadLogging();
		}
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @return
	 */
	public String queryDownloadProgress(String path) {
		List<DownloadProgress> items = new ArrayList<DownloadProgress>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		LogUtil.d(this, "开始-------- 读取------数据库   thread id"
				+ Thread.currentThread().getId());
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ").append(MultiDownload.THREAD_ID).append(", ")
				.append(MultiDownload.DOWN_LENGTH).append(" FROM ")
				.append(MultiDownload.TABLE_NAME).append(" WHERE ")
				.append(MultiDownload.DOWN_PATH).append(" =?");
		Cursor cursor = db.rawQuery(sb.toString(), new String[] { path });
		while (cursor.moveToNext()) {
			DownloadProgress pro = new DownloadProgress();
			pro.threadId = cursor.getString(cursor
					.getColumnIndex(MultiDownload.THREAD_ID));
			pro.downloadProgress = cursor.getString(cursor
					.getColumnIndex(MultiDownload.DOWN_LENGTH));
			items.add(pro);
		}
		LogUtil.d(this, "结束-------- 读取------数据库  thread id"
				+ +Thread.currentThread().getId());
		cursor.close();
		return JsonUtil.toJson(items);
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public void insertDownloadProgress(String path, Map<String, String> map) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		LogUtil.d(this, "开始-------- 插入------数据库");
		db.beginTransaction();
		try {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				ContentValues values = new ContentValues();
				values.put(MultiDownload.DOWN_LENGTH, entry.getValue());
				values.put(MultiDownload.THREAD_ID, entry.getKey());
				values.put(MultiDownload.DOWN_PATH, path);
				db.insert(MultiDownload.TABLE_NAME, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			LogUtil.d(this, "结束-------- 插入------数据库");
			db.endTransaction();
		}
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 *            entry的key是线程id， value是已下载长度
	 */
	public void updateDownloadProgress(String path, Map<String, String> map) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		LogUtil.d(this, "开始-------- 更新------数据库   thread id"
				+ Thread.currentThread().getId());
		db.beginTransaction();
		try {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				ContentValues values = new ContentValues();
				values.put(MultiDownload.DOWN_LENGTH, entry.getValue());
				db.update(MultiDownload.TABLE_NAME, values,
						MultiDownload.DOWN_PATH + " = ? AND "
								+ MultiDownload.THREAD_ID + " = ?",
						new String[] { path, entry.getKey() });
			}
			db.setTransactionSuccessful();
		} finally {
			LogUtil.d(this, "结束-------- 更新------数据库  thread id"
					+ +Thread.currentThread().getId());
			db.endTransaction();
		}
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 * @param path
	 */
	public void deleteDownloadProgress(String path) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		LogUtil.d(this, "开始-------- 删除------数据库");
		db.delete(MultiDownload.TABLE_NAME, MultiDownload.DOWN_PATH + " = ?",
				new String[] { path });
		LogUtil.d(this, "结束-------- 删除------数据库");
	}

	/**
	 * 关闭数据库 ，在程序退出的时候记得关闭.
	 */
	public void close() {
		mDbHelper.close();
	}
}

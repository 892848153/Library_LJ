package com.lj.library.dao;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lj.library.dao.table.MultiDownload;

/**
 * 
 * @time 2014年10月10日 下午5:32:22
 * @author jie.liu
 */
public class DBManager {

	private final DBHelper mDbHelper;

	public DBManager(Context context) {
		mDbHelper = new DBHelper(context);
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @return
	 */
	public Map<String, String> getData(String path) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + MultiDownload.THREAD_ID + ", "
				+ MultiDownload.DOWN_LENGTH + " FROM "
				+ MultiDownload.TABLE_NAME + " WHERE "
				+ MultiDownload.DOWN_PATH + " = ?", new String[] { path });
		Map<String, String> data = new HashMap<String, String>();
		while (cursor.moveToNext()) {
			data.put(cursor.getString(cursor
					.getColumnIndex(MultiDownload.THREAD_ID)),
					cursor.getString(cursor
							.getColumnIndex(MultiDownload.DOWN_LENGTH)));
		}
		db.close();
		return data;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public void save(String path, Map<Integer, Integer> map) {// int threadid,
																// int position
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db.execSQL(
						"insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
						new Object[] { path, entry.getKey(), entry.getValue() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		db.close();
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public void update(String path, Map<Integer, Integer> map) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();

		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db.execSQL(
						"update filedownlog set downlength=? where downpath=? and threadid=?",
						new Object[] { entry.getValue(), path, entry.getKey() });
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		db.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 * @param path
	 */
	public void delete(String path) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("delete from filedownlog where downpath=?",
				new Object[] { path });
		db.close();
	}
}

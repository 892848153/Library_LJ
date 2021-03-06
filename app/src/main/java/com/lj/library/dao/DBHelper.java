package com.lj.library.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lj.library.dao.table.MultiDownload;

/**
 * 创建数据库.
 * 
 * @time 2014年10月10日 下午5:32:14
 * @author jie.liu
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String DBNAME = "library.db";
	private static final int VERSION = 1;

	private static DBHelper sInstance;

	public DBHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	/**
	 * 一个实例就是一个跟数据库的链接.
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static DBHelper getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DBHelper(context);
		}
		return sInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MultiDownload.CREATE_TABLE_DOWN_LOG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(MultiDownload.DROP_TABLE_DOWN_LOG);
		// 更新数据库保存旧数据http://87426628.blog.163.com/blog/static/6069361820131069485844/
		onCreate(db);
	}
}

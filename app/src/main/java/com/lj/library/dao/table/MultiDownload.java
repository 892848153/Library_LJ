package com.lj.library.dao.table;

/**
 * 多线程断点续传下载表.
 * 
 * @time 2014年10月31日 上午10:17:16
 * @author jie.liu
 */
public class MultiDownload {

	public static final String TABLE_NAME = "downLog";

	public static final String _ID = "_id";

	public static final String DOWN_PATH = "downPath";

	public static final String THREAD_ID = "threadId";

	public static final String DOWN_LENGTH = "downLength";

	public static final String DROP_TABLE_DOWN_LOG = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	public static final String CREATE_TABLE_DOWN_LOG = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ " ("
			+ _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DOWN_PATH
			+ " VARCHAR, "
			+ THREAD_ID
			+ " VARCHAR, "
			+ DOWN_LENGTH
			+ " VARCHAR)";
}

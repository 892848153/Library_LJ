package com.lj.library.util;

import java.io.File;

import android.os.Environment;

/**
 * @time 2014年6月9日 上午11:33:20
 * @author liuzenglong163@gmail.com
 */

public class SDCardUtil {

	/**
	 * 
	 * 获取SDCard路径.
	 * 
	 * @return 返回SDCard绝对路径，如果SDCard没有挂载，则返回""
	 */
	public static String getSDPath() {
		File sdDir = null;
		// 判断sd卡是否存在
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
		}
		if (sdDir != null) {
			return sdDir.toString();
		} else {
			return "";
		}
	}
}

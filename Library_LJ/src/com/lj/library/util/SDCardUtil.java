package com.lj.library.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Environment;
import android.text.TextUtils;

/**
 * 获取SDCard路径类.
 * 
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
			return Environment.getRootDirectory().getAbsolutePath();
		}
	}

	/**
	 * 利用linux命名获取sdcard路径.
	 * 
	 * @return
	 */
	public static List<String> getSDPathFromCmd() {
		List<String> sdcardPaths = new ArrayList<String>();
		BufferedInputStream in = null;
		BufferedReader inBr = null;
		String cmd = "cat /proc/mounts";
		Runtime run = Runtime.getRuntime();// 返回与当前 Java 应用程序相关的运行时对象
		try {
			Process p = run.exec(cmd);// 启动另一个进程来执行命令
			in = new BufferedInputStream(p.getInputStream());
			inBr = new BufferedReader(new InputStreamReader(in));

			String lineStr;
			while ((lineStr = inBr.readLine()) != null) {
				// 获得命令执行后在控制台的输出信息
				LogUtil.i("SDCardUtil:getSDCardPath", lineStr);

				String[] temp = TextUtils.split(lineStr, " ");
				// 得到的输出的第二个空格后面是路径
				String result = temp[1];
				File file = new File(result);
				if (file.isDirectory() && file.canRead() && file.canWrite()) {
					LogUtil.d("directory can read can write:",
							file.getAbsolutePath());
					// 可读可写的文件夹未必是sdcard，我的手机的sdcard下的Android/obb文件夹也可以得到
					sdcardPaths.add(result);
				}
			}

			// 检查命令是否执行失败。
			if (p.waitFor() != 0 && p.exitValue() == 1) {
				// p.exitValue()==0表示正常结束，1：非正常结束
				LogUtil.i("SDCardUtil:getSDCardPath", "命令执行失败!");
			}
		} catch (Exception e) {
			LogUtil.i("SDCardUtil:getSDCardPath", e.toString());

			sdcardPaths.add(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
		} finally {
			IOStreamCloser.closeReader(inBr);
			IOStreamCloser.closeInputStream(in);
		}

		optimize(sdcardPaths);
		for (Iterator<String> iterator = sdcardPaths.iterator(); iterator
				.hasNext();) {
			String string = iterator.next();
			LogUtil.i("清除过后", string);
		}
		return sdcardPaths;
	}

	private static void optimize(List<String> sdcaredPaths) {
		if (sdcaredPaths.size() == 0) {
			return;
		}
		int index = 0;
		while (true) {
			if (index >= sdcaredPaths.size() - 1) {
				String lastItem = sdcaredPaths.get(sdcaredPaths.size() - 1);
				for (int i = sdcaredPaths.size() - 2; i >= 0; i--) {
					if (sdcaredPaths.get(i).contains(lastItem)) {
						sdcaredPaths.remove(i);
					}
				}
				return;
			}

			String containsItem = sdcaredPaths.get(index);
			for (int i = index + 1; i < sdcaredPaths.size(); i++) {
				if (sdcaredPaths.get(i).contains(containsItem)) {
					sdcaredPaths.remove(i);
					i--;
				}
			}

			index++;
		}
	}
}

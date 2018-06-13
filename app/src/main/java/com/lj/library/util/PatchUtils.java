package com.lj.library.util;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * 类说明： 	APK Patch工具类.
 *
 */
public class PatchUtils {

	/**
	 * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath 示例:/sdcard/old.apk
	 * @param newApkPath 示例:/sdcard/new.apk
	 * @param patchPath  示例:/sdcard/xx.patch
	 * @return 返回0表示合成成功
	 */
	public static native int patch(String oldApkPath, String newApkPath, String patchPath);

	/**
	 * 获取合成的新的APK文件保存的地址.
	 *
	 * @param context
	 * @param newVersionCode 新apk的版本号
     * @return
     */
	public static String getNewApkFilePath(@NonNull Context context, int newVersionCode) {
		StringBuilder sb = new StringBuilder();
		sb.append(StorageUtils.getAppCacheRootDirPath())
				.append("/update/apk/")
				.append(AppUtils.getVerCode(context))
				.append("to")
				.append(newVersionCode);

		return EncryptionUtils.getMD5(sb.toString()) + ".apk";
	}

	public static String getPatchFilePath(@NonNull Context context, int newVersionCode) {
		StringBuilder sb = new StringBuilder();
		sb.append(StorageUtils.getAppCacheRootDirPath())
				.append("/update/patch/")
				.append(AppUtils.getVerCode(context))
				.append("to")
				.append(newVersionCode);

		return EncryptionUtils.getMD5(sb.toString()) + ".patch";
	}
}
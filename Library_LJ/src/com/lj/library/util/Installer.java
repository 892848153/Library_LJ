package com.lj.library.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 安装APK类 .
 * 
 * @time 2014年11月11日 下午4:24:26
 * @author jie.liu
 */
public class Installer {

	private InstallCallback mCallback;

	/**
	 * 普通安装.
	 * 
	 * @param context
	 * @param filePath
	 * @return
	 */
	public boolean normalInstall(Context context, String filePath) {
		if (!isFilePathValid(filePath)) {
			return false;
		}

		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.setDataAndType(Uri.parse("file://" + filePath),
				"application/vnd.android.package-archive");
		installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(installIntent);
		return true;
	}

	/**
	 * 静默安装
	 * 
	 * @param filePath
	 * @return
	 */
	public void slientInstall(final String filePath, InstallCallback callback) {
		mCallback = callback;
		if (!isFilePathValid(filePath)) {
			if (mCallback != null) {
				mCallback.onInstallResult(false);
			}
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean result = false;
				OutputStream os = null;
				DataOutputStream dos = null;
				BufferedInputStream bufferIs = null;
				BufferedReader bufferReader = null;
				try {
					Runtime runtime = Runtime.getRuntime();
					Process process = runtime.exec("su");
					bufferIs = new BufferedInputStream(process.getInputStream());
					bufferReader = new BufferedReader(new InputStreamReader(
							bufferIs));
					os = process.getOutputStream();
					dos = new DataOutputStream(os);
					LogUtil.i("", "chmod 777 " + filePath + "\n");
					String chmod = "chmod 777 " + filePath + "\n";
					dos.write(chmod.getBytes());
					dos.flush();
					String install = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
							+ filePath;
					dos.write(install.getBytes());
					dos.flush();
					LogUtil.i("", "检查命令是否执行失败。");
					// 检查命令是否执行失败。
					if (process.waitFor() != 0 && process.exitValue() == 1) {
						// p.exitValue()==0表示正常结束，1：非正常结束
						LogUtil.i("", "命令执行失败!");
						// result = false;
					} else {
						LogUtil.i("", "命令执行成功!");
					}

					String lineStr = null;
					LogUtil.i("", "开始获取控制台输出信息");
					while ((lineStr = bufferReader.readLine()) != null) {
						// 获得命令执行后在控制台的输出信息
						LogUtil.i("", "控制台信息:" + lineStr);
						result = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
					result = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
					result = false;
				} finally {
					IOStreamCloser.closeOutputStream(dos);
					IOStreamCloser.closeOutputStream(os);
					IOStreamCloser.closeReader(bufferReader);
					IOStreamCloser.closeInputStream(bufferIs);

					if (mCallback != null) {
						mCallback.onInstallResult(result);
					}
				}
			}
		}).start();
	}

	private boolean isFilePathValid(String filePath) {
		File file = new File(filePath);
		if (file == null || !file.exists() || !file.isFile()
				|| file.length() <= 0) {
			LogUtil.e("Installer", filePath + "文件的路径无效");
			return false;
		}
		return true;
	}

	public interface InstallCallback {
		void onInstallResult(boolean isSuccess);
	}

	// /**
	// * 静默安装.
	// *
	// * @param context
	// * @param filePath
	// * @return
	// */
	// public static int installSilent(Context context, String filePath) {
	// return installSilent(context, filePath, "-r");
	// }
	//
	// /**
	// * install package silent by root
	// *
	// *
	// *
	// * Attentions:
	// *
	// * Don't call this on the ui thread, it may costs some times.
	// *
	// *
	// * You should add android.permission.INSTALL_PACKAGES in manifest, so no
	// * need to request root permission, if you are system app.
	// *
	// *
	// *
	// *
	// * @param context
	// * @param filePath
	// * file path of package
	// * @param pmParams
	// * pm install params
	// * @return {@link DwInstallApkUtils#INSTALL_SUCCEEDED} means install
	// * success, other means failed. details see
	// * {@link DwInstallApkUtils}.INSTALL_FAILED_*. same to
	// * {@link PackageManager}.INSTALL_*
	// */
	// public static int installSilent(Context context, String filePath,
	// String pmParams) {
	// if (filePath == null || filePath.length() == 0) {
	// return INSTALL_FAILED_INVALID_URI;
	// }
	//
	// File file = new File(filePath);
	// if (file == null || file.length() <= 0 || !file.exists()
	// || !file.isFile()) {
	// return INSTALL_FAILED_INVALID_URI;
	// }
	//
	// /**
	// * if context is system app, don't need root permission, but should add
	// * * android:name="android.permission.INSTALL_PACKAGES" /> in mainfest
	// **/
	// StringBuilder command = new StringBuilder()
	// .append("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install ")
	// .append(pmParams == null ? "" : pmParams).append(" ")
	// .append(filePath.replace(" ", "\\ "));
	// CommandResult commandResult = execCommand(command.toString(),
	// !isSystemApplication(context), true);
	// if (commandResult.successMsg != null
	// && (commandResult.successMsg.contains("Success") ||
	// commandResult.successMsg
	// .contains("success"))) {
	// return INSTALL_SUCCEEDED;
	// }
	//
	// Log.e(TAG,
	// new StringBuilder().append("installSilent successMsg:")
	// .append(commandResult.successMsg).append(", ErrorMsg:")
	// .append(commandResult.errorMsg).toString());
	// if (commandResult.errorMsg == null) {
	// return INSTALL_FAILED_OTHER;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_ALREADY_EXISTS")) {
	// return INSTALL_FAILED_ALREADY_EXISTS;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_APK")) {
	// return INSTALL_FAILED_INVALID_APK;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_INVALID_URI")) {
	// return INSTALL_FAILED_INVALID_URI;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")) {
	// return INSTALL_FAILED_INSUFFICIENT_STORAGE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_DUPLICATE_PACKAGE"))
	// {
	// return INSTALL_FAILED_DUPLICATE_PACKAGE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_NO_SHARED_USER")) {
	// return INSTALL_FAILED_NO_SHARED_USER;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_UPDATE_INCOMPATIBLE")) {
	// return INSTALL_FAILED_UPDATE_INCOMPATIBLE;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_SHARED_USER_INCOMPATIBLE")) {
	// return INSTALL_FAILED_SHARED_USER_INCOMPATIBLE;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_MISSING_SHARED_LIBRARY")) {
	// return INSTALL_FAILED_MISSING_SHARED_LIBRARY;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_REPLACE_COULDNT_DELETE")) {
	// return INSTALL_FAILED_REPLACE_COULDNT_DELETE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_DEXOPT")) {
	// return INSTALL_FAILED_DEXOPT;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_OLDER_SDK")) {
	// return INSTALL_FAILED_OLDER_SDK;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_CONFLICTING_PROVIDER")) {
	// return INSTALL_FAILED_CONFLICTING_PROVIDER;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_NEWER_SDK")) {
	// return INSTALL_FAILED_NEWER_SDK;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_TEST_ONLY")) {
	// return INSTALL_FAILED_TEST_ONLY;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_CPU_ABI_INCOMPATIBLE")) {
	// return INSTALL_FAILED_CPU_ABI_INCOMPATIBLE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_MISSING_FEATURE")) {
	// return INSTALL_FAILED_MISSING_FEATURE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_CONTAINER_ERROR")) {
	// return INSTALL_FAILED_CONTAINER_ERROR;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_INVALID_INSTALL_LOCATION")) {
	// return INSTALL_FAILED_INVALID_INSTALL_LOCATION;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_MEDIA_UNAVAILABLE"))
	// {
	// return INSTALL_FAILED_MEDIA_UNAVAILABLE;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_VERIFICATION_TIMEOUT")) {
	// return INSTALL_FAILED_VERIFICATION_TIMEOUT;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_FAILED_VERIFICATION_FAILURE")) {
	// return INSTALL_FAILED_VERIFICATION_FAILURE;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_PACKAGE_CHANGED")) {
	// return INSTALL_FAILED_PACKAGE_CHANGED;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_UID_CHANGED")) {
	// return INSTALL_FAILED_UID_CHANGED;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_PARSE_FAILED_NOT_APK")) {
	// return INSTALL_PARSE_FAILED_NOT_APK;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_BAD_MANIFEST")) {
	// return INSTALL_PARSE_FAILED_BAD_MANIFEST;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION")) {
	// return INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_NO_CERTIFICATES")) {
	// return INSTALL_PARSE_FAILED_NO_CERTIFICATES;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES")) {
	// return INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING")) {
	// return INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME")) {
	// return INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID")) {
	// return INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_MANIFEST_MALFORMED")) {
	// return INSTALL_PARSE_FAILED_MANIFEST_MALFORMED;
	// }
	// if (commandResult.errorMsg
	// .contains("INSTALL_PARSE_FAILED_MANIFEST_EMPTY")) {
	// return INSTALL_PARSE_FAILED_MANIFEST_EMPTY;
	// }
	// if (commandResult.errorMsg.contains("INSTALL_FAILED_INTERNAL_ERROR")) {
	// return INSTALL_FAILED_INTERNAL_ERROR;
	// }
	// return INSTALL_FAILED_OTHER;
	// }
	//
	// // 同时此处需要执行shell 命令：
	//
	// public static boolean execCommand(String[] commands) {
	// int result = -1;
	// if (commands == null || commands.length == 0) {
	// return false;
	// }
	//
	// Process process = null;
	// BufferedReader successResult = null;
	// BufferedReader errorResult = null;
	// StringBuilder successMsg = null;
	// StringBuilder errorMsg = null;
	//
	// DataOutputStream os = null;
	// try {
	// process = Runtime.getRuntime().exec();
	// os = new DataOutputStream(process.getOutputStream());
	// for (String command : commands) {
	// if (command == null) {
	// continue;
	// }
	//
	// // donnot use os.writeBytes(commmand), avoid chinese charset
	// // error
	// os.write(command.getBytes());
	// os.writeBytes(COMMAND_LINE_END);
	// os.flush();
	// }
	// os.writeBytes(COMMAND_EXIT);
	// os.flush();
	//
	// result = process.waitFor();
	// // get command result
	// if (isNeedResultMsg) {
	// successMsg = new StringBuilder();
	// errorMsg = new StringBuilder();
	// successResult = new BufferedReader(new InputStreamReader(
	// process.getInputStream()));
	// errorResult = new BufferedReader(new InputStreamReader(
	// process.getErrorStream()));
	// String s;
	// while ((s = successResult.readLine()) != null) {
	// successMsg.append(s);
	// }
	// while ((s = errorResult.readLine()) != null) {
	// errorMsg.append(s);
	// }
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (os != null) {
	// os.close();
	// }
	// if (successResult != null) {
	// successResult.close();
	// }
	// if (errorResult != null) {
	// errorResult.close();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// if (process != null) {
	// process.destroy();
	// }
	// }
	// return false;
	// }
}

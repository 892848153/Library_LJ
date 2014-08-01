package com.lj.library.util;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lj.library.application.MyApplication;
import com.lj.library.bean.UserInfo;

public class ContextUtil {

	public static void pushToActivity(Context from, Class<?> to) {
		Intent intent = new Intent(from, to);
		from.startActivity(intent);
	}

	public static void pushToActivity(Context from, Class<?> to, Bundle bundle) {
		Intent intent = new Intent(from, to);
		intent.putExtras(bundle);
		from.startActivity(intent);
	}

	public static void pushToActivity(Context from, Intent intent) {
		from.startActivity(intent);
	}

	/**
	 * 如果没有登录即跳到登录页面.
	 * 
	 * @param from
	 * @param to
	 */
	public static void pushToActivityWithLogin(Context from, Class<?> to) {
		Intent intent = null;
		UserInfo userInfo = MyApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			// intent = new Intent(from, LoginActivity.class);
		} else {
			intent = new Intent(from, to);
		}

		from.startActivity(intent);
	}

	public static void pushToActivityWithLogin(Context from, Class<?> to,
			Bundle bundle) {
		Intent intent = null;
		UserInfo userInfo = MyApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			// intent = new Intent(from, LoginActivity.class);
		} else {
			intent = new Intent(from, to);
		}

		intent.putExtras(bundle);
		from.startActivity(intent);
	}

	public static void pushToActivityWithLogin(Context context, Intent intent) {
		UserInfo userInfo = MyApplication.getInstance().getUserInfo();
		if (userInfo == null) {
			// intent = new Intent(context, LoginActivity.class);
		}

		context.startActivity(intent);
	}

	public static String getIMEI(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * 
	 * 检测Intent是否会被接收.
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	/**
	 * 因此键盘.
	 * 
	 * @param activity
	 * @param editText
	 */
	public void hideSystemKeyboard(Activity activity, EditText editText) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}

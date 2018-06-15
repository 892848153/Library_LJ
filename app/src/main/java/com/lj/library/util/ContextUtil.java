package com.lj.library.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lj.library.application.MyApplication;
import com.lj.library.bean.UserInfo;

import java.util.List;


public class ContextUtil {

	private static Intent sTargetIntent;

	private static Class<?> sTargetClass;

	private static Bundle sBundle;

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
		if (intent.resolveActivity(from.getPackageManager()) != null) {
			from.startActivity(intent);
		}
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
//		UserInfo userInfo = SampleApplicationLike.getInstance().getUserInfo();
		if (userInfo == null) {
			sTargetClass = to;
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
//		UserInfo userInfo = SampleApplicationLike.getInstance().getUserInfo();
		if (userInfo == null) {
			sTargetClass = to;
			sBundle = bundle;
			// intent = new Intent(from, LoginActivity.class);
		} else {
			intent = new Intent(from, to);
			intent.putExtras(bundle);
		}

		from.startActivity(intent);
	}

	public static void pushToActivityWithLogin(Context context, Intent intent) {
		UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//		UserInfo userInfo = SampleApplicationLike.getInstance().getUserInfo();
		if (userInfo == null) {
			sTargetIntent = intent;
			// intent = new Intent(context, LoginActivity.class);
		}

		context.startActivity(intent);
	}

	/**
	 * 跳到先前需要跳转的页面.
	 * <p/>
	 * 调用 {@link #pushToActivityWithLogin(Context, Class)} ,
	 * {@link #pushToActivityWithLogin(Context, Intent)},
	 * {@link #pushToActivityWithLogin(Context, Class, Bundle)}
	 * 方法跳转界面，结果没有跳转到想要的界面，而是跳转到登陆页面。登陆成功后，可以调用此方法 继续跳转到先前想要跳转到的页面(跳转所带的参数依然存在)。
	 *
	 * @param from
	 */
	public static void pushToRecentlyActivity(Context from) {
		if (sTargetClass == null && sTargetIntent == null) {
			return;
		}

		UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//		UserInfo userInfo = SampleApplicationLike.getInstance().getUserInfo();
		if (userInfo != null) {
			performPushToRecentlyActivity(from);

			sTargetIntent = null;
			sTargetClass = null;
			sBundle = null;
		}
	}

	private static void performPushToRecentlyActivity(Context from) {
		if (sTargetClass != null) {
			if (sBundle != null) {
				pushToActivity(from, sTargetClass, sBundle);
			} else {
				pushToActivity(from, sTargetClass);
			}
		}

		if (sTargetIntent != null) {
			pushToActivity(from, sTargetIntent);
		}
	}

	public static String getIMEI(Context context) {
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return ((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} else {
			return null;
		}
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
	 * 隐藏键盘.
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

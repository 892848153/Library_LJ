package com.lj.library.application;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.lj.library.bean.UserInfo;
import com.lj.library.constants.Constants;
import com.lj.library.util.PreferenceUtil;

public class MyApplication extends Application {

	private final List<Activity> mActivityList = new LinkedList<Activity>();

	private static MyApplication sInstance;

	private UserInfo mUserInfo;

	public static synchronized MyApplication getInstance() {
		if (null == sInstance) {
			sInstance = new MyApplication();
		}
		return sInstance;

	}

	public void addActivity(Activity activity) {
		mActivityList.add(activity);
	}

	/**
	 * 删除实例.
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		Iterator<Activity> iterator = mActivityList.iterator();
		while (iterator.hasNext()) {
			Activity targetActivity = iterator.next();
			if (targetActivity == activity) {
				iterator.remove();
			}
		}
	}

	/**
	 * 退出APP.
	 */
	public void exit() {
		for (Activity activity : mActivityList) {
			activity.finish();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		PreferenceUtil util = new PreferenceUtil(this,
				Constants.PREFERENCE_FILE);
		UserInfo userInfo = util.getUserInfo();
		// 有用户信息, 说明此时是登录状态
		String userId = userInfo.userId;
		if (!TextUtils.isEmpty(userId)) {
			storeUserInfo(userInfo);
		}
		// Thread.setDefaultUncaughtExceptionHandler(new
		// MyUncaughtExceptionHandler());
		util = null;
	}

	/**
	 * 
	 * 缓存用户信息，有内存和外村缓存.
	 * 
	 * @param userInfo
	 *            传入null表示退出
	 */
	public void storeUserInfo(UserInfo userInfo) {
		mUserInfo = userInfo;

		if (userInfo == null) {
			// 退出
			PreferenceUtil util = new PreferenceUtil(this,
					Constants.PREFERENCE_FILE);
			util.setUserInfo(UserInfo.getEmptyInstance());
		} else {
			new PreferenceUtil(this, Constants.PREFERENCE_FILE)
					.setUserInfo(userInfo);
		}
	}

	/**
	 * 
	 * 获取用户的缓存信息.
	 * 
	 * @return 用户没有登录则返回null
	 */
	public UserInfo getUserInfo() {
		if (mUserInfo != null) {
			return mUserInfo;
		}

		PreferenceUtil util = new PreferenceUtil(sInstance,
				Constants.PREFERENCE_FILE);
		UserInfo userInfo = util.getUserInfo();
		String userId = userInfo.userId;
		if (!TextUtils.isEmpty(userId)) {
			// 说明此时是登录状态
			storeUserInfo(userInfo);
			return mUserInfo;
		} else {
			// 未登录，返回null
			return null;
		}
	}

	/**
	 * 判断是否登录.
	 */
	public boolean doJugdgeLogin() {
		if (getUserInfo() == null) {
			return false;
		} else {
			return true;
		}
	}
}

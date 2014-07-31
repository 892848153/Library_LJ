package com.lj.library.application;

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
	 * @time 2014年5月20日 上午11:50:00
	 * @author jie.liu
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
	 * @time 2014年5月20日 上午11:50:37
	 * @author jie.liu
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
	 * @todo 判断是否登录
	 * @time 2014-5-22 下午4:26:03
	 * @author liuzenglong163@gmail.com
	 */
	public boolean doJugdgeLogin(Activity activity) {
		if (getUserInfo() == null) {
			return false;
		} else {
			return true;
		}
	}
}

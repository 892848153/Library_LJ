package com.lj.library.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.lj.library.bean.UserInfo;

public class PreferenceUtil {

	private final SharedPreferences sp;
	private final SharedPreferences.Editor editor;

	@SuppressLint("CommitPrefEdits")
	public PreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public boolean isFirstRun() {
		return sp.getBoolean("isFirstRun", true);
	}

	public void setIsFirstRun(boolean isFirstOpen) {
		editor.putBoolean("isFirstRun", isFirstOpen);
		editor.commit();
	}

	public String getFlashImageUrl() {
		return sp.getString("flashUrl", "");
	}

	public void setFlashImageUrl(String url) {
		editor.putString("flashUrl", url);
		editor.commit();
	}

	public void setUserInfo(UserInfo userInfo) {
		editor.putString("userId", userInfo.userId);
		editor.commit();
	}

	public UserInfo getUserInfo() {
		String userId = sp.getString("userId", "");
		UserInfo userInfo = new UserInfo();
		userInfo.userId = userId;
		return userInfo;
	}

	public void setUsername(String username) {
		editor.putString("username", username);
		editor.commit();
	}

	public String getUsername() {
		return sp.getString("username", "");
	}

	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}

	public String getPassword() {
		return sp.getString("password", "");
	}
}

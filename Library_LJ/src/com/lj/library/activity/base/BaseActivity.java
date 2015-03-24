package com.lj.library.activity.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lj.library.application.MyApplication;

/**
 * 基础类.
 * 
 * @time 2015年3月6日 上午10:55:47
 * @author jie.liu
 */
@SuppressLint("Registered")
public class BaseActivity extends FragmentActivity {

	protected Activity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		MyApplication.getInstance().addActivity(mContext);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().removeActivity(mContext);
	}
}

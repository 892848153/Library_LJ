package com.lj.library.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lj.library.application.MyApplication;

/**
 * 基础类，使用Fragment的类要继承这个.
 * 
 * @time 2015年3月24日 下午2:13:48
 * @author jie.liu
 */
public class BaseFragmentActivity extends FragmentActivity {

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
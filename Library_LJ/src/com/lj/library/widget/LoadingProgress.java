package com.lj.library.widget;

import android.app.Activity;
import android.app.ProgressDialog;

import com.lj.library.util.LogUtil;

public class LoadingProgress {

	private ProgressDialog mDialog;

	private int mShownCount;

	/**
	 * Singleton类被装载了，instance不一定被初始化。因为SingletonHolder类没有被主动使用，
	 * 只有显示通过调用getInstance方法时，才会显示装载SingletonHolder类，从而实例化instance。
	 * 又由于只有一行返回的代码，所以保证了线程安全.
	 * 
	 * @return
	 */
	public static LoadingProgress getInstance() {
		return SingletonHolder.sInstance;
	}

	private static class SingletonHolder {
		private static LoadingProgress sInstance = new LoadingProgress();
	}

	private LoadingProgress() {
	}

	public void show(Activity context) {
		show(context, "正在加载中...");
	}

	public void show(Activity context, String message) {
		if (haveShown()) {
			mShownCount++;
			return;
		}

		createLoadingDialog(context, message);

		if (!mDialog.isShowing()) {
			LogUtil.i(context, "显示ProgressDialog");
			mShownCount++;
			mDialog.show();
		}
	}

	private boolean haveShown() {
		return mShownCount != 0 ? true : false;
	}

	private void createLoadingDialog(Activity context, String message) {
		mDialog = new ProgressDialog(context);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setIndeterminate(false);
		mDialog.setInverseBackgroundForced(false);
		mDialog.setMessage(message);
	}

	public void dismiss() {
		if (mDialog != null && mDialog.isShowing()) {
			LogUtil.i(this, "关闭ProgressDialog");
			mShownCount = 0;
			mDialog.cancel();
			mDialog = null;
		}
	}

}

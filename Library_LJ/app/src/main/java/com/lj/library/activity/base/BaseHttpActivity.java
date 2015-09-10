package com.lj.library.activity.base;

import com.lj.library.http.HttpHelper.OnHttpCallback;
import com.lj.library.util.LogUtil;
import com.lj.library.util.Toaster;
import com.lj.library.widget.LoadingProgress;

/**
 * 网络请求基础类.
 * 
 * @time 2015年3月12日 下午3:57:50
 * @author jie.liu
 */
public class BaseHttpActivity extends BaseActivity implements OnHttpCallback {

	@Override
	public void onHttpNetworkNotFound(String path) {
		Toaster.showShort(this, "未检测到可用的网络");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpReturn(String path, int response, String result) {

	}

	@Override
	public void onHttpError(String path, Exception exception) {
		Toaster.showShort(this, "网络连接异常，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpError(String path, int response) {
		Toaster.showShort(this, "网络连接超时，请重试");
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpSuccess(String path, String result) {
		StringBuilder sb = new StringBuilder();
		sb.append("请求路径：").append(path).append("\n").append(result);
		LogUtil.d(this, sb.toString());
		dismissDialogIfNeeded();
	}

	@Override
	public void onHttpNothingReturn(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("请求路径：").append(path).append("\n").append("什么也没有返回");
		LogUtil.d(this, sb.toString());
		dismissDialogIfNeeded();
	}

	private void dismissDialogIfNeeded() {
		// 如果有启动进度条对话框将被关闭
		LoadingProgress.getInstance().dismiss();
	}
}

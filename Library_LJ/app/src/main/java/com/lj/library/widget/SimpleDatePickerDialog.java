package com.lj.library.widget;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * 自定义日期选择对话框.
 * 
 * 该对话框选中日期后只会触发一次onDateSetListener回调。 {@link DatePickerDialog}
 * 会触发两次。在日期选中后触发一次，在 {@link #onStop()}中触发一次
 * 
 * @time 2015年1月29日 上午10:37:18
 * @author jie.liu
 */
public class SimpleDatePickerDialog extends DatePickerDialog {

	public SimpleDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme, callBack, year, monthOfYear, dayOfMonth);
	}

	public SimpleDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	@Override
	protected void onStop() {
		// super.onStop();
	}
}

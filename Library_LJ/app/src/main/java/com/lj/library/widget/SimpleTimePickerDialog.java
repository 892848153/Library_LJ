package com.lj.library.widget;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * 自定义时间选择对话框.
 * 
 * 该对话框选中时间后只会触发一次onTimeSetListener回调。 {@link TimePickerDialog}
 * 会触发两次。在时间选中后触发一次，在 {@link #onStop()}中触发一次
 * 
 * @time 2015年1月29日 上午10:42:21
 * @author jie.liu
 */
public class SimpleTimePickerDialog extends TimePickerDialog {

	public SimpleTimePickerDialog(Context context, int theme,
			OnTimeSetListener callBack, int hourOfDay, int minute,
			boolean is24HourView) {
		super(context, theme, callBack, hourOfDay, minute, is24HourView);
	}

	public SimpleTimePickerDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
	}

	@Override
	protected void onStop() {
		// super.onStop();
	}
}

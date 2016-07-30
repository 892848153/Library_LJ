package com.lj.library.util;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * 按比例改变View的高度.这个类有问题，待完善
 * 
 * @time 2015年5月7日 下午3:43:31
 * @author jie.liu
 */
public class ViewScaledUtils {

	public static void scaleViewHeight(final View view, final int widthWeight,
			final int heightWeight) {
		if (widthWeight < 0 || heightWeight < 0) {
			throw new IllegalArgumentException("widthWeight和heightWeight必须大于0");
		}

		// view.requestLayout();
		// int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE,
		// MeasureSpec.AT_MOST);
		// view.measure(measureSpec, measureSpec);
		// int measureWidth = view.getMeasuredWidth();
		view.post(new Runnable() {
			@Override
			public void run() {
				int rowWidth = view.getWidth();
				LogUtil.i("", "控件的宽是:" + rowWidth + "   measureWidth  ");
				// 如果有设置宽高比例，就按比例决定高的大小
				if (rowWidth > 0 && widthWeight > 0 && heightWeight > 0) {
					LayoutParams params = view.getLayoutParams();
					params.height = heightWeight * rowWidth / widthWeight;
					LogUtil.i("", "控件的高算出来是:" + params.height);
					view.setLayoutParams(params);
					LogUtil.i("", "控件的高" + view.getHeight());
					view.getParent().requestLayout();
					view.requestLayout();
					view.invalidate();
				}
			}
		});
		// int rowWidth = view.getWidth();
		// LogUtil.i("", "控件的宽是:" + rowWidth);
		// // 如果有设置宽高比例，就按比例决定高的大小
		// if (rowWidth > 0 && widthWeight > 0 && heightWeight > 0) {
		// LayoutParams params = view.getLayoutParams();
		// params.height = heightWeight * rowWidth / widthWeight;
		// LogUtil.i("", "控件的高算出来是:" + params.height);
		// view.setLayoutParams(params);
		// }
	}
}

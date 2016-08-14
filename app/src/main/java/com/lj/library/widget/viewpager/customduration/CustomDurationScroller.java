package com.lj.library.widget.viewpager.customduration;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CustomDurationScroller extends Scroller {

	private double mScrollFactor = 1;

	public CustomDurationScroller(Context context) {
		super(context);
	}

	public CustomDurationScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	/**
	 * 
	 * @param scrollFactor
	 *            设置切换页面的时间倍数
	 */
	public void setScrollDurationFactor(double scrollFactor) {
		mScrollFactor = scrollFactor;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy,
				(int) (duration * mScrollFactor));
	}

}

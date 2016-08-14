package com.lj.library.widget.viewpager.customduration;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import com.lj.library.util.LogUtil;

import java.lang.reflect.Field;

public class CustomDurationViewPager extends ViewPager {

	private CustomDurationScroller mScroller = null;

	public CustomDurationViewPager(Context context) {
		super(context);
		postInitViewPager();
	}

	public CustomDurationViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		postInitViewPager();
	}

	/**
	 * Override the Scroller instance with our own class so we can change the
	 * duration
	 */
	private void postInitViewPager() {
		LogUtil.e(this, "执行 postInitViewPager" + "");
		try {
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			Field interpolator = viewpager.getDeclaredField("sInterpolator");
			interpolator.setAccessible(true);
			mScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolator.get(null));
			scroller.set(this, mScroller);
		} catch (Exception e) {
		}
	}

	public void setScrollDurationFactor(double scrollFactor) {
		mScroller.setScrollDurationFactor(scrollFactor);
	}

}
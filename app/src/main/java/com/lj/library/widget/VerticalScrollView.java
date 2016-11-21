package com.lj.library.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.lj.library.util.LogUtil;

/**
 * 垂直滑动控件.
 * <p/>
 * 在此控件中可以包含水平滑动的控件(eg: {@link ViewPager})， 滑动事件已做处理，互不冲突.
 * 
 * @time 2015年1月27日 下午5:22:24
 * @author jie.liu
 */
public class VerticalScrollView extends ScrollView {

	private float mLastXIntercept = 0f;

	private float mLastYIntercept = 0f;

	private boolean mIsVerticalScrolling;

	private boolean mIsScrolling;

	public VerticalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			LogUtil.i(this, "onInterceptTouchEvent  --  ACTION_DOWN");
			mLastXIntercept = event.getX();
			mLastYIntercept = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			LogUtil.i(this, "onInterceptTouchEvent  ------  ACTION_MOVE");
			if (mIsScrolling == false) {
				LogUtil.d(this, "isScrolling  --> false");
				int scaledTouchSlop = ViewConfiguration.get(getContext())
						.getScaledTouchSlop();
				float xDelta = Math.abs(mLastXIntercept - event.getRawX());
				float yDelta = Math.abs(mLastYIntercept - event.getRawY());
				if (yDelta >= scaledTouchSlop || xDelta >= scaledTouchSlop) {
					if (Math.abs(yDelta) > Math.abs(xDelta)) {
						mIsVerticalScrolling = true;
						LogUtil.d(this, "isVerticalScrolling  --> true");
					} else {
						LogUtil.d(this, "isVerticalScrolling  --> false");
						mIsVerticalScrolling = false;
					}
					mIsScrolling = true;
					LogUtil.d(this, "isScrolling  --> true");
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			LogUtil.i(this, "onInterceptTouchEvent  ----------  ACTION_UP");
		case MotionEvent.ACTION_CANCEL:
			LogUtil.i(this, "onInterceptTouchEvent  ----------  ACTION_CANCEL");
			mIsVerticalScrolling = false;
			mIsScrolling = false;
			mLastXIntercept = 0f;
			mLastYIntercept = 0f;
			break;
		default:
			break;
		}

		return super.onInterceptTouchEvent(event) && mIsVerticalScrolling;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_UP:
			LogUtil.i(this, "onTouchEvent  --  ACTION_UP");
		case MotionEvent.ACTION_CANCEL:
			LogUtil.i(this, "onTouchEvent  --  ACTION_CANCEL");
			mIsVerticalScrolling = false;
			mIsScrolling = false;
			mLastXIntercept = 0f;
			mLastYIntercept = 0f;
			break;
		}
		return super.onTouchEvent(ev);
	}
}

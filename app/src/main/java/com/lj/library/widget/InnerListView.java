package com.lj.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

import com.lj.library.util.LogUtil;

/**
 * 在SrollView中的ListView.
 * <p/>
 * 带有根据内容测算自己高度， 解决滑动冲突的功能.
 * 
 * @time 2015年5月7日 下午5:58:27
 * @author jie.liu
 */
public class InnerListView extends ListView {

	private ScrollView mParentScrollView;

	public InnerListView(Context context) {
		super(context);
	}

	public InnerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InnerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 第一种方法
		// int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
		// MeasureSpec.AT_MOST);

		// 第二种方法，待验证
		int expandSpec = MeasureSpec
				.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setParentScrollAble(false);// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
			// 停住不能滚动
			LogUtil.d(this, "onInterceptTouchEvent down");
		case MotionEvent.ACTION_MOVE:
			LogUtil.d(this, "onInterceptTouchEvent move");
			break;
		case MotionEvent.ACTION_UP:
			LogUtil.d(this, "onInterceptTouchEvent up");
		case MotionEvent.ACTION_CANCEL:
			LogUtil.d(this, "onInterceptTouchEvent cancel");
			setParentScrollAble(true);// 当手指松开时，让父ScrollView重新拿到onTouch权限
			break;
		default:
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 是否把滚动事件交给父scrollview
	 * 
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag) {
		if (mParentScrollView != null) {
			// 这里的parentScrollView就是listview外面的那个scrollview
			mParentScrollView.requestDisallowInterceptTouchEvent(!flag);
		}
	}

	public void setParentScrollView(ScrollView scrollView) {
		mParentScrollView = scrollView;
	}
}

package com.lj.library.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 
 * @time 2014年8月6日 上午9:56:08
 * @author jie.liu
 */
/**
 * 
 * @time 2014年8月6日 上午9:56:10
 * @author jie.liu
 */
public class MyViewPager extends ViewGroup {

	private static final String TAG = MyViewPager.class.getSimpleName();

	private VelocityTracker mVelocityTracker; // 用于判断甩动手势

	private static final int SNAP_VELOCITY = 300;

	private Scroller mScroller;

	private int mCurScreen;

	private float mLastMotionX;

	private OnPageChangeListener mOnPageChangeListener;

	private PagerAdapter mPagerAdapter;

	public MyViewPager(Context context) {
		super(context);
		init(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = 0;
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurScreen * width, 0);
	}

	/**
	 * 根据当前页面在屏幕中所占的百分比，自动判断并滑动到合适的页面.
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	/**
	 * 平滑的滑动到目标页.
	 * 
	 * @param whichScreen
	 */
	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta));
			invalidate(); // Redraw the layout
		}
	}

	/**
	 * 不带动画效果的滑动到目标页.
	 * 
	 * @param whichScreen
	 */
	public void snapToScreenWithoutAnim(int whichScreen) {
		mCurScreen = whichScreen;
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			scrollTo(whichScreen * getWidth(), 0);
			invalidate(); // Redraw the layout
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}

		int residue = getScrollX() % getWidth();
		int result = getScrollX() / getWidth();
		if (residue == 0 && result != mCurScreen) {
			mCurScreen = result;
			if (mOnPageChangeListener != null) {
				Log.e(TAG, "onVeiwChange     scrollX" + getScrollX()
						+ "   currenScreen:" + mCurScreen);
				mOnPageChangeListener.OnPageChange(mCurScreen);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.i("", "onTouchEvent  ACTION_DOWN");
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}

			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int deltaX = (int) (mLastMotionX - x);
			if (isCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}

				mLastMotionX = x;
				scrollBy(deltaX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i("", "onTouchEvent  ACTION_UP");
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			break;
		}

		return true;
	}

	private boolean isCanMove(int deltaX) {
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}

		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}

		return true;
	}

	public void SetOnViewChangeListener(OnPageChangeListener listener) {
		mOnPageChangeListener = listener;
	}

	/**
	 * 
	 * @param adapter
	 * @param shouldCleanChildren
	 *            是否清除当前的子控件
	 */
	public void setPagerAdapter(PagerAdapter adapter,
			boolean shouldCleanChildren) {
		mPagerAdapter = adapter;
		refreshPager(shouldCleanChildren);
	}

	/**
	 * 调用此方法会清除当前的子控件.
	 * 
	 * @param adapter
	 */
	public void setPagerAdapter(PagerAdapter adapter) {
		mPagerAdapter = adapter;
		refreshPager(true);
	}

	private void refreshPager(boolean shouldCleanChildren) {
		if (shouldCleanChildren) {
			removeAllViews();
		}

		int size = mPagerAdapter.getCount();
		for (int i = 0; i < size; i++) {
			View view = (View) mPagerAdapter.getItem(i);
			addView(view);
		}
		requestLayout();
	}

	public void setCurrentScreen(int currentScreen) {
		mCurScreen = currentScreen;
		requestLayout();
	}

	public int getCurrentPage() {
		return mCurScreen;
	}

	public int getPageCount() {
		return mPagerAdapter.getCount();
	}

	public PagerAdapter getPagerAdapter() {
		return mPagerAdapter;
	}
}

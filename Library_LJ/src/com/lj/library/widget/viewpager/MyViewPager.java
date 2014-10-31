package com.lj.library.widget.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.lj.library.util.LogUtil;

/**
 * 自定义ViewPager.
 * 
 * @time 2014年8月6日 上午9:56:10
 * @author jie.liu
 */
public class MyViewPager<T> extends ViewGroup {

	private VelocityTracker mVelocityTracker; // 用于判断甩动手势

	private Scroller mScroller;

	private int mCurScreen;

	private int mScrollDuration = 500;

	private float mLastMotionX;

	private OnPageChangeListener mOnPageChangeListener;

	private PagerAdapter<T> mPagerAdapter;

	/******************** 解决滑动冲突开始 *******************************/
	private List<ViewGroup> mParentViews;

	private float mXoriginal = 0f;

	private float mYoriginal = 0f;

	private boolean mMaybeClickEvent = false;
	/******************** 解决滑动冲突 *******************************/

	private static final int SNAP_VELOCITY = 300;

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
		if (mScroller == null) {
			mScroller = new Scroller(context);
		}
		if (mParentViews == null) {
			mParentViews = new ArrayList<ViewGroup>();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 注释掉changed是因为，多次调用setAdapter方法，changed为false，第二次调用增加的View不会显示出来.
		// if (changed) {
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
		// }
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 获取孩子中最高的
		int maxChildHeight = getMaxChildHeight(widthMeasureSpec);
		int finalHeight = maxChildHeight;
		Drawable background = getBackground();
		if (background != null) {
			int backHeight = background.getMinimumHeight();
			// 孩子中最高的跟背景图片取最高的值为控件的高度
			finalHeight = Math.max(maxChildHeight, backHeight);
		}

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(finalHeight,
				MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 重新测量孩子的高度
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurScreen * width, 0);
	}

	private int getMaxChildHeight(int widthMeasureSpec) {
		int height = 1;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			// 为了获取孩子的高度，所以设置成 MeasureSpec.UNSPECIFIED模式
			child.measure(widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			if (h > height)
				height = h;
		}
		return height;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		LogUtil.i(this, "onInterceptTouchEvent");
		final int action = event.getAction();
		final float x = event.getX();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			LogUtil.d(this, "onInterceptTouchEvent ACTION_DOWN");
			// 当手指触到控件的时候，让父ScrollView交出ontouch权限，也就是让父scrollview停住不能滚动
			setParentScrollable(false);

			// 为判断是否是点击事件做准备
			mMaybeClickEvent = true;
			mXoriginal = event.getRawX();
			mYoriginal = event.getRawY();

			// 滑动事件的处理
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
			LogUtil.d(this, "onInterceptTouchEvent ACTION_MOVE");
			// 判断是否是点击事件
			int scaledTouchSlop = ViewConfiguration.get(getContext())
					.getScaledTouchSlop();
			float xDelta = Math.abs(mXoriginal - event.getRawX());
			float yDelta = Math.abs(mYoriginal - event.getRawY());
			if (xDelta >= scaledTouchSlop || yDelta >= scaledTouchSlop) {
				LogUtil.d(this, "是滑动事件，不是点击事件");
				// 说明是滑动事件，不会是点击事件.点击事件是down->up，但是用户很容易出现move，所以允许稍微的move
				mMaybeClickEvent = false;
			}

			// 滑动事件处理
			int deltaX = calcDeltaX((int) (mLastMotionX - x));
			if (isCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}

				mLastMotionX = x;
				scrollBy(deltaX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			LogUtil.d(this, "onInterceptTouchEvent ACTION_UP");
			// 滑动事件处理
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}

			int attachScreen = calcAttachScreen();
			if (velocityX > SNAP_VELOCITY && attachScreen > 0) {
				LogUtil.i(this, "snap left");
				if (mCurScreen - attachScreen == 1) {
					// 左侧页面属于attachScreen的时候
					snapToScreen(attachScreen);
				} else {
					// 当前页或右侧界面属于attachScrren的时候
					snapToScreen(attachScreen - 1);
				}
			} else if (velocityX < -SNAP_VELOCITY
					&& attachScreen < getChildCount() - 1) {
				LogUtil.i(this, "snap right");
				if (attachScreen - mCurScreen == 1) {
					// 右侧界面属于attachScreen的时候
					snapToScreen(attachScreen);
				} else {
					// 当前页或左侧界面属于attachScreen的时候
					snapToScreen(attachScreen + 1);
				}
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
		case MotionEvent.ACTION_CANCEL:
			LogUtil.d(this, "onInterceptTouchEvent ACTION_CANCEL");
			// 当手指松开时，让父ScrollView重新拿到onTouch权限
			setParentScrollable(true);
			break;
		default:
			mMaybeClickEvent = false;
			break;
		}

		// 这两个动作是最后的动作，所以在这决定要不要把事件传递下去， 如果是点击事件就传递下去
		// 因为点击事件是ACTION_DOWN->ACTION_UP-CLICK, 所以会触发
		if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			LogUtil.i(this, "maybeClick:" + mMaybeClickEvent);
			if (mMaybeClickEvent) {
				return false;
			} else {
				return true;
			}
		}

		// 不是ACTION_UP或者ACTION_CANCEL时，则不是最后一个动作，
		// 不是最后一个动作时，不拦截，交给孩子处理
		return false;
	}

	/**
	 * 
	 * 是否把滚动事件交给父Scrollview.
	 * 
	 * @param flag
	 */
	private void setParentScrollable(boolean flag) {
		for (ViewGroup parentView : mParentViews) {
			parentView.requestDisallowInterceptTouchEvent(!flag);
		}
	}

	/**
	 * 增加包含它的可滑动的View，这样可以使滑动动作不冲突.
	 * 
	 * @param parentView
	 */
	public void addParentScrollView(ViewGroup parentView) {
		if (!mParentViews.contains(parentView)) {
			mParentViews.add(parentView);
		}
	}

	/**
	 * 由于手指滑动的偏移量可能过大，调用{@link #scrollBy(int, int)}之后会使内容溢出， 所以在这里需要更改下.
	 * 
	 * @param originalDeltaX
	 * @return
	 */
	private int calcDeltaX(int originalDeltaX) {
		int resultDelta = originalDeltaX;
		int scrollX = getScrollX();
		// 左滑时
		if (resultDelta <= 0 && Math.abs(resultDelta) > scrollX) {
			resultDelta = -scrollX;
		}

		// 右滑时
		int slidingDistance = ((getChildCount() - 1) * getWidth())
				- getScrollX();
		if (resultDelta > 0 && resultDelta > slidingDistance) {
			resultDelta = slidingDistance;
		}
		return resultDelta;
	}

	private boolean isCanMove(int deltaX) {
		if (getScrollX() <= 0 && deltaX <= 0) {
			return false;
		}

		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX >= 0) {
			return false;
		}

		return true;
	}

	/**
	 * 根据当前页面在屏幕中所占的百分比，自动判断并滑动到合适的页面.
	 */
	public void snapToDestination() {
		final int destScreen = calcAttachScreen();
		if (destScreen > 0 && destScreen < mPagerAdapter.getCount())
			snapToScreen(destScreen);
	}

	/**
	 * 根据当前的偏移量计算出当前显示的应该算是哪一页.
	 * <p/>
	 * 哪个页面占据控件的二分之一以上就认为这页是当前显示的
	 * 
	 * @return
	 */
	private int calcAttachScreen() {
		final int screenWidth = getWidth();
		final int attachScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		return attachScreen;
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
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					getScrollDuration(delta));
			invalidate();
		}
	}

	private int getScrollDuration(int delta) {
		if (mScrollDuration < 100) {
			return Math.abs(delta);
		} else {
			return mScrollDuration;
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
				// LogUtil.i(this, "onVeiwChange     scrollX" + getScrollX()
				// + "   currenScreen:" + mCurScreen);
				mOnPageChangeListener.OnPageChange(mCurScreen);
			}
		}
	}

	public boolean isScrolling() {
		return getScrollX() % getWidth() == 0 ? false : true;
	}

	public void SetOnViewChangeListener(OnPageChangeListener listener) {
		if (mOnPageChangeListener != listener) {
			mOnPageChangeListener = listener;
		}
	}

	/**
	 * 调用此方法会清除当前的子控件.
	 * 
	 * @param adapter
	 */
	public void setAdapter(PagerAdapter<T> adapter) {
		setAdapter(adapter, true);
	}

	/**
	 * 
	 * @param adapter
	 * @param shouldCleanChildren
	 *            是否清除当前的子控件
	 */
	public void setAdapter(PagerAdapter<T> adapter, boolean shouldCleanChildren) {
		if (adapter == null) {
			throw new NullPointerException("PagerAdapter is null");
		}

		if (mPagerAdapter == adapter) {
			return;
		}

		setCurrentScreen(0);
		mPagerAdapter = adapter;
		refreshPager(adapter, shouldCleanChildren);
	}

	private void refreshPager(PagerAdapter<T> pagerAdapter,
			boolean shouldCleanChildren) {
		if (shouldCleanChildren) {
			removeAllViews();
		}

		int size = mPagerAdapter.getCount();
		for (int i = 0; i < size; i++) {
			View view = mPagerAdapter.getItem(i);
			addView(view);
		}
		requestLayout();
	}

	public void setCurrentScreen(int currentScreen) {
		if (mCurScreen != currentScreen) {
			mCurScreen = currentScreen;
			requestLayout();
		}
	}

	public void setScrollDuration(int mills) {
		mScrollDuration = mills;
	}

	public int getCurrentScreen() {
		return mCurScreen;
	}

	public int getPageCount() {
		return mPagerAdapter.getCount();
	}

	public PagerAdapter<T> getPagerAdapter() {
		return mPagerAdapter;
	}
}

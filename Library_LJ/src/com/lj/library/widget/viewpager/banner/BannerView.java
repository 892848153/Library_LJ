package com.lj.library.widget.viewpager.banner;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 广告栏.
 * 
 * @time 2014年8月6日 上午10:22:16
 * @author jie.liu
 */

public class BannerView extends RelativeLayout implements OnPageChangeListener {

	private Context mContext;

	private MyViewPager mViewPager;

	private LinearLayout mIndicatorLlyt;

	private ImageView imgs[];

	private OnPageChangeListener mOutListener;

	private boolean mCycleScrollable;

	private int mPageCount;

	private Handler mHandler;

	private long mInterval = 3000;

	private final int WHAT_AUTO_CYCLE = 0;

	public BannerView(Context context) {
		super(context);
		init(context);
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public BannerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mHandler = new InternalHandler(this);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		inflater.inflate(R.layout.banner_view, this);
		mViewPager = (MyViewPager) findViewById(R.id.view_pager);
		mIndicatorLlyt = (LinearLayout) findViewById(R.id.indicator_llyt);
		mViewPager.SetOnViewChangeListener(this);
	}

	/**
	 * 传递进来的页面必须四页以上才能轮播，有效页数是两页.
	 */
	public void enableCycleScroll() {
		if (mCycleScrollable == true || mPageCount < 4) // 传递进来的页面必须四页以上才能轮播，有效页数是两页
			return;

		mCycleScrollable = true;
		createIndicators();
		setCurrentPage(1);
	}

	public void SetOnViewChangeListener(OnPageChangeListener listener) {
		mOutListener = listener;
	}

	/**
	 * 调用此方法会清除当前包含的子控件.
	 * 
	 * @param adapter
	 */
	public void setPagerAdapter(DefaultPagerAdapter adapter) {
		setPagerAdapter(adapter, true);
	}

	/**
	 * 
	 * @param adapter
	 * @param shouldCleanChildren
	 *            是否清除当前包含的子控件
	 */
	public void setPagerAdapter(DefaultPagerAdapter adapter,
			boolean shouldCleanChildren) {
		mPageCount = adapter.getCount();
		mViewPager.setPagerAdapter(adapter, shouldCleanChildren);
		createIndicators();
	}

	/**
	 * 显示提示点，默认是显示的.
	 */
	public void showIndicators() {
		mIndicatorLlyt.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏提示点,默认是显示的.
	 */
	public void hideIndicator() {
		mIndicatorLlyt.setVisibility(View.GONE);
	}

	private void createIndicators() {
		mIndicatorLlyt.removeAllViews();
		int indicatorCount = calcIndicatorCount();
		imgs = new ImageView[indicatorCount];
		for (int i = 0; i < indicatorCount; i++) {
			ImageView img = new ImageView(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 5, 5, 5);
			img.setImageResource(R.drawable.page_indicator_bg);
			mIndicatorLlyt.addView(img, params);
			imgs[i] = img;
		}
		imgs[0].setEnabled(false);
	}

	private int calcIndicatorCount() {
		int indicatorCount = 0;
		if (mCycleScrollable) {
			indicatorCount = mPageCount - 2;
		} else {
			indicatorCount = mPageCount;
		}
		return indicatorCount;
	}

	/**
	 * 设置每个点的margin.
	 * 
	 * @param margin
	 */
	public void setIndicatorMargin(int margin) {
		setIndicatorMargin(margin, margin, margin, margin);
	}

	/**
	 * 设置每个点的margin.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setIndicatorMargin(int left, int top, int right, int bottom) {
		int count = imgs.length;
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) imgs[i]
					.getLayoutParams();
			params.setMargins(left, top, right, bottom);
		}
	}

	@Override
	public void OnPageChange(int currentPage) {
		int realCurrentPage = calcRealCurrPage(currentPage);
		Log.e("CurrentPage", currentPage + "");
		Log.e("realCurrentPage", realCurrentPage + "");

		setcurrentPoint(realCurrentPage);
		sendAutoCycleMsg();
		snapToRightPage(currentPage);

		if (mOutListener != null) {
			mOutListener.OnPageChange(realCurrentPage);
		}
	}

	/**
	 * 计算真是情况的第几页，用于显示标识点.
	 * 
	 * @param currentPage
	 * @return
	 */
	private int calcRealCurrPage(int currentPage) {
		int realCurrentPage = (currentPage % mPageCount) - 1;
		if (realCurrentPage == -1) {
			realCurrentPage = mPageCount - 3;
			return realCurrentPage;
		}
		if (realCurrentPage == mPageCount - 2) {
			realCurrentPage = 0;
			return realCurrentPage;
		}
		return realCurrentPage;
	}

	private void setcurrentPoint(int position) {
		if (position < 0 || position > mPageCount - 1) {
			return;
		}

		int length = imgs.length;
		for (int i = 0; i < length; i++) {
			imgs[i].setEnabled(true);
			if (i == position) {
				imgs[i].setEnabled(false);
			}
		}
	}

	private void snapToRightPage(int currentPage) {
		int snapPage = calcSnapPage(currentPage);
		if (currentPage == 0 || currentPage == mPageCount - 1) {
			mViewPager.snapToScreenWithoutAnim(snapPage);
		}
	}

	/**
	 * 计算需要滑动到的页面,用于首尾页的时候滑动.
	 * 
	 * @param currentPage
	 * @return
	 */
	private int calcSnapPage(int currentPage) {
		int snapPage = currentPage;
		if (snapPage == 0) {
			snapPage = mPageCount - 2;
			return snapPage;
		}
		if (snapPage == mPageCount - 1) {
			snapPage = 1;
			return snapPage;
		}
		return snapPage;
	}

	public void setCurrentPage(int currentPage) {
		mViewPager.setCurrentScreen(currentPage);
	}

	public void startAutoCycle() {
		sendAutoCycleMsg();
	}

	private void sendAutoCycleMsg() {
		clearAutoCycleMsgs();
		mHandler.sendEmptyMessageDelayed(WHAT_AUTO_CYCLE, mInterval);
	}

	public void stopAutoCycle() {
		clearAutoCycleMsgs();
	}

	private void clearAutoCycleMsgs() {
		if (mHandler.hasMessages(WHAT_AUTO_CYCLE)) {
			mHandler.removeMessages(WHAT_AUTO_CYCLE);
		}
	}

	/**
	 * 设置滚动时间间隔.
	 * 
	 * @param interval
	 *            时间间隔，单位是毫秒
	 */
	public void setCycleInterval(long interval) {
		mInterval = interval;
		stopAutoCycle();
		startAutoCycle();
	}

	private static class InternalHandler extends Handler {

		private final WeakReference<BannerView> mViewRef;

		public InternalHandler(BannerView view) {
			mViewRef = new WeakReference<BannerView>(view);
		}

		@Override
		public void handleMessage(Message msg) {
			BannerView bannerView = mViewRef.get();
			if (bannerView != null) {
				bannerView.mViewPager.snapToScreen(bannerView.mViewPager
						.getCurrentPage() + 1);
			}

		}
	}
}

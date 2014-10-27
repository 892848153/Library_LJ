package com.lj.library.widget.viewpager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.view.View;

/**
 * 为MyViewPager提供参数.
 * 
 * @time 2014年8月5日 下午3:59:52
 * @author jie.liu
 * @param <T>
 */
public abstract class PagerAdapter<T> {

	private List<View> mPageViews;

	private List<T> mData;

	private boolean mCycleScroll;

	public PagerAdapter() {
		mPageViews = new ArrayList<View>();
	}

	public void init(List<T> datas, boolean cycleScroll) {
		mData = datas;
		mCycleScroll = cycleScroll;
		getViews();
	}

	public void setPages(List<View> views) {
		mPageViews = new ArrayList<View>(views);
	}

	/**
	 * 返回页面数量.
	 * <p/>
	 * 在页面为无限循环滑动模式下，返回的值会比数据量多两个.
	 * 
	 * @return
	 */
	public int getCount() {
		return mPageViews.size();
	}

	public int getItemPosition(View view) {
		return mPageViews.indexOf(view);
	}

	public View getItem(int position) {
		return mPageViews.get(position);
	}

	public List<View> getPages() {
		return mPageViews;
	}

	public T getData(int position) {
		return mData.get(position);
	}

	private List<View> getViews() {
		if (mData == null || mData.size() <= 0) {
			mPageViews = Collections.<View> emptyList();
			return mPageViews;
		}

		int size = mData.size();
		for (int i = 0; i < size; i++) {
			View view = getView(i, mData.get(i));
			// 设置可点击 MyViewPager才可以接收到手指的move事件
			view.setClickable(true);
			mPageViews.add(view);
		}

		if (mCycleScroll && size >= 2) {
			// TODO 无限循环模式下，第一页和最后一页是其他页的影子，若本体是可变的，比如点击变色。则这两页也需要跟着变。
			// 所以最佳做法是对本地控件调用getDrawableCach，得到图片，设置在首尾两页。可保证影子页与本体页一致
			// ImageView firstView = new ImageView(mContext);
			// ImageView lastView = new ImageView(mContext);

			View firstView = getView(0, mData.get(0));
			View lastView = getView(size - 1, mData.get(size - 1));
			// 设置可点击 MyViewPager才可以接收到手指的move事件
			firstView.setClickable(true);
			lastView.setClickable(true);

			mPageViews.add(0, lastView);
			mPageViews.add(firstView);
		}
		return mPageViews;
	}

	/**
	 * 
	 * @param position
	 *            数据的下标
	 * @param data
	 *            对应数据项
	 * @return
	 */
	public abstract View getView(int position, T data);

	/**
	 * {@link PagerAdapter}的默认实现.
	 * <p/>
	 * 
	 * 默认实现类中的{@link View}列表的获取方式是构造函数传值进来.
	 * 
	 * @time 2014年10月27日 下午5:43:26
	 * @author jie.liu
	 */
	public static class DefaultPagerAdapter<T> extends PagerAdapter<T> {

		public DefaultPagerAdapter() {
			setPages(new ArrayList<View>());
		}

		public DefaultPagerAdapter(List<View> views) {
			setPages(views);
		}

		/**
		 * 不可调用此方法，此方法在改类中返回null，所以也不可以调用
		 * {@link PagerAdapter#init(List, boolean)}方法.
		 */
		@Override
		public View getView(int p, T data) {
			return null;
		}
	}
}

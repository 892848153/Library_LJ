package com.lj.library.widget.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

/**
 * 为MyViewPager提供参数.
 * 
 * @time 2014年8月5日 下午3:59:52
 * @author jie.liu
 */
public class PagerAdapter {

	private List<View> mPageViews;

	public PagerAdapter() {
		mPageViews = new ArrayList<View>();
	}

	public PagerAdapter(List<View> views) {
		mPageViews = new ArrayList<View>(views);
	}

	public void setPages(List<View> views) {
		mPageViews = new ArrayList<View>(views);
	}

	public int getCount() {
		return mPageViews.size();
	}

	public int getItemPosition(Object object) {
		return mPageViews.indexOf(object);
	}

	public Object getItem(int position) {
		return mPageViews.get(position);
	}

	public List<View> getPages() {
		return mPageViews;
	}
}

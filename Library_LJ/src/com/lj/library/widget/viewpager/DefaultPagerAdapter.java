package com.lj.library.widget.viewpager;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

/**
 * 
 * @time 2014年8月5日 下午3:59:52
 * @author jie.liu
 */

public class DefaultPagerAdapter {

	private final List<View> mPageViews;

	public DefaultPagerAdapter() {
		mPageViews = new ArrayList<View>();
	}

	public DefaultPagerAdapter(List<View> views) {
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
}

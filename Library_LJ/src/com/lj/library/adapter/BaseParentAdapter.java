package com.lj.library.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Adapter基类.
 * 
 * @time 2014年7月30日 下午4:17:32
 * @author jie.liu
 */
public class BaseParentAdapter extends BaseAdapter {

	public List<?> mItems;
	public LayoutInflater mInflater;
	public Activity mActivity;
	public int mLayoutRes;

	public BaseParentAdapter(List<?> mList, Activity mActivity) {
		super();
		this.mItems = mList;
		this.mActivity = mActivity;
		this.mInflater = LayoutInflater.from(mActivity);
	}

	public BaseParentAdapter(List<?> mList, Activity mActivity, int layoutRes) {
		this.mItems = mList;
		this.mActivity = mActivity;
		this.mInflater = LayoutInflater.from(mActivity);
		this.mLayoutRes = layoutRes;
	}

	@Override
	public int getCount() {
		if (null != mItems) {
			return mItems.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (null != mItems) {
			return mItems.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (null != mItems) {
			return position;
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return null;
	}
}

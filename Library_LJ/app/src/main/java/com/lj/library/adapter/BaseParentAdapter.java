package com.lj.library.adapter;

import java.util.List;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * Adapter基类.
 * 
 * @time 2014年7月30日 下午4:17:32
 * @author jie.liu
 */
public abstract class BaseParentAdapter<V> extends BaseAdapter {

	protected List<V> mItems;
	protected LayoutInflater mInflater;
	protected Activity mActivity;
	protected int mLayoutId;

	public BaseParentAdapter(List<V> items, Activity activity) {
		super();
		this.mItems = items;
		this.mActivity = activity;
		this.mInflater = LayoutInflater.from(activity);
	}

	public BaseParentAdapter(List<V> items, Activity activity, int layoutRes) {
		this.mItems = items;
		this.mActivity = activity;
		this.mInflater = LayoutInflater.from(activity);
		this.mLayoutId = layoutRes;
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

	/**
	 * 用法.
	 * <p/>
	 * 
	 * <pre>
	 * if (convertView == null) {
	 * 	convertView = LayoutInflater.from(getActivity()).inflate(
	 * 			R.layout.fragment_feed_item, parent, false);
	 * }
	 * 
	 * ImageView thumnailView = getAdapterView(convertView, R.id.video_thumbnail);
	 * </pre>
	 * 
	 * @param convertView
	 * @param id
	 *            控件id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getAdapterView(View convertView, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			convertView.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = convertView.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}

package com.lj.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 可以根据宽度变化，按比例改变高度的ImageView.
 * 
 * @time 2014年10月29日 上午10:22:44
 * @author jie.liu
 */
public class ScaledImageView extends ImageView {

	private float mWidthWeight = 0f;
	private float mHeightWeight = 0f;

	public ScaledImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaledImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaledImageView(Context context) {
		super(context);
	}

	public void setFrameWeight(float widthWeight, float heightWeight) {
		if (mWidthWeight == widthWeight && mHeightWeight == heightWeight) {
			return;
		}

		mWidthWeight = widthWeight;
		mHeightWeight = heightWeight;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		// 如果有设置宽高比例，就按比例决定高的大小
		if (measuredWidth >= 0 && mWidthWeight > 0 && mHeightWeight > 0) {
			setMeasuredDimension(measuredWidth, (int) (mHeightWeight
					/ mWidthWeight * measuredWidth));
		}
	}
}

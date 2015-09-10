package com.lj.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lj.library.widget.frameweight.FrameWeightConfig;

/**
 * 可以根据宽度变化，按比例改变高度的ImageView.
 * 
 * @time 2014年10月29日 上午10:22:44
 * @author jie.liu
 */
public class ScaledImageView extends ImageView {

	private final FrameWeightConfig mConfig = new FrameWeightConfig();

	public ScaledImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaledImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScaledImageView(Context context) {
		super(context);
	}

	/**
	 * 设置宽高比例.
	 * 
	 * @param widthWeight
	 *            宽的比重, 必须大于0
	 * @param heightWeight
	 *            高的比重,必须大于0
	 */
	public void setFrameWeight(float widthWeight, float heightWeight) {
		mConfig.setFrameWeight(widthWeight, heightWeight);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		// 如果有设置宽高比例，就按比例决定高的大小
		float widthWeight = mConfig.getWidthWeight();
		float heightWeight = mConfig.getHeightWeight();
		if (measuredWidth >= 0 && widthWeight > 0 && heightWeight > 0) {
			setMeasuredDimension(measuredWidth, (int) (heightWeight
					* measuredWidth / widthWeight));
		}
	}
}

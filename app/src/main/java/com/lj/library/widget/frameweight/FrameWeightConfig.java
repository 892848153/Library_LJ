package com.lj.library.widget.frameweight;

/**
 * 高根据宽的大小按比例设置.
 * 
 * @time 2015年4月1日 下午2:56:13
 * @author jie.liu
 */
public class FrameWeightConfig {

	private float mWidthWeight = 0f;
	private float mHeightWeight = 0f;

	/**
	 * 设置宽高比例.
	 * 
	 * @param widthWeight
	 *            宽的比重, 必须大于0
	 * @param heightWeight
	 *            高的比重,必须大于0
	 */
	public void setFrameWeight(float widthWeight, float heightWeight) {
		if (mWidthWeight == widthWeight && mHeightWeight == heightWeight) {
			return;
		}

		if (widthWeight < 0 || heightWeight < 0) {
			throw new IllegalArgumentException("widthWeight和heightWeight必须大于0");
		}

		mWidthWeight = widthWeight;
		mHeightWeight = heightWeight;
	}

	/**
	 * 获取宽的比重.
	 * 
	 * @return
	 */
	public float getWidthWeight() {
		return mWidthWeight;
	}

	/**
	 * 获取高的比重.
	 * 
	 * @return
	 */
	public float getHeightWeight() {
		return mHeightWeight;
	}
}

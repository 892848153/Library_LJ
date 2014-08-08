package com.lj.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lj.library.R;

/**
 * 自定义dialog 可以控制dialog大小与动画.
 * <p/>
 * 对话的高默认为{@link ViewGroup.LayoutParams#WRAP_CONTENT}
 * 
 * @author jie.liu
 */
public class MyDialog extends Dialog {

	private int mDialogWidth;
	private boolean mIsAnimation = false;
	private int mAnimationStyle = R.style.DialogAnimation;
	/** 对话框的宽度占屏幕的百分比 **/
	private float mWidthPercent;

	private final static float WIDTH_PERCENT_DEFAULT = 0.8f;

	public MyDialog(Context context) {
		this(context, WIDTH_PERCENT_DEFAULT);
	}

	/**
	 * 
	 * @param context
	 * @param widthPercent
	 *            对话框的宽度占屏幕的百分比
	 */
	public MyDialog(Context context, float widthPercent) {
		this(context, 0, 0);
		mDialogWidth = calcDialogWidth(context, widthPercent);
	}

	private int calcDialogWidth(Context context, float widthPercent) {
		if (widthPercent == 0) {
			mWidthPercent = WIDTH_PERCENT_DEFAULT;
		} else {
			mWidthPercent = widthPercent;
		}

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		return (int) (displayMetrics.widthPixels * mWidthPercent);
	}

	public MyDialog(Context context, int windowDialogWidth) {
		this(context, R.style.BaseDialogTheme, windowDialogWidth);
	}

	public MyDialog(Context context, int theme, int windowDialogWidth) {
		super(context, theme);
		this.mDialogWidth = windowDialogWidth;
	}

	public MyDialog setAnimation(boolean isAnimation) {
		this.mIsAnimation = isAnimation;
		return this;
	}

	public void setAnimationStyle(int animationStyle) {
		this.mIsAnimation = true;
		this.mAnimationStyle = animationStyle;
	}

	public void createDialog(View view) {
		setContentView(view);
		initDialog();
	}

	public void createDialog(int layoutResID) {
		setContentView(layoutResID);
		initDialog();
	}

	public void showDialog(View view) {
		setContentView(view);
		initDialog();
		show();
	}

	public void showDialog(int layoutResID) {
		setContentView(layoutResID);
		initDialog();
		show();
	}

	private void initDialog() {
		Window window = getWindow(); // 得到对话框
		if (mIsAnimation)
			window.setWindowAnimations(mAnimationStyle);
		if (mDialogWidth > 0) {
			WindowManager.LayoutParams params = window.getAttributes();
			params.width = mDialogWidth;
			// 不要设置高度，默认是wrap_content
			// params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			params.dimAmount = 0.5f; // 该变量指示后面的窗口变暗的程度, 1.0表示完全不透明，0.0表示透明。
			window.setAttributes(params);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		}
	}
}

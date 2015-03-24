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
	private int mAnimationStyle;
	/** 对话框的宽度占屏幕的百分比 **/
	private float mWidthPercent;

	private final static float WIDTH_PERCENT_DEFAULT = 0.8f;

	public MyDialog(Context context) {
		this(context, WIDTH_PERCENT_DEFAULT);
	}

	/**
	 * @param context
	 * @param widthPercent
	 *            对话框的宽度占屏幕的百分比
	 */
	public MyDialog(Context context, float widthPercent) {
		this(context, R.style.BaseDialogTheme, 0); // 主题传入0,调用系统主题
		mDialogWidth = calcDialogWidth(context, widthPercent);
	}

	private int calcDialogWidth(Context context, float widthPercent) {
		if (widthPercent < 0) {
			throw new IllegalArgumentException("widthPercent必须大于等于0");
		}
		if (widthPercent == 0) {
			mWidthPercent = WIDTH_PERCENT_DEFAULT;
		} else {
			mWidthPercent = widthPercent;
		}

		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		return (int) (displayMetrics.widthPixels * mWidthPercent);
	}

	/**
	 * @param context
	 * @param dialogWidthDP
	 *            对话框宽度，单位dp
	 */
	public MyDialog(Context context, int dialogWidthDP) {
		this(context, R.style.BaseDialogTheme, dialogWidthDP);
	}

	/**
	 * @param context
	 * @param theme
	 * @param dialogWidthDP
	 *            对话框宽度，单位dp
	 */
	public MyDialog(Context context, int theme, int dialogWidthDP) {
		super(context, theme);
		if (dialogWidthDP <= 0) {
			throw new IllegalArgumentException("widthPercent必须大于0");
		}
		DisplayMetrics displayMetrics = context.getResources()
				.getDisplayMetrics();
		this.mDialogWidth = (int) (dialogWidthDP * displayMetrics.density);
	}

	public MyDialog setAnimation(boolean isAnimation) {
		this.mIsAnimation = isAnimation;
		return this;
	}

	public void setAnimationStyle(int animationStyle) {
		this.mIsAnimation = true;
		this.mAnimationStyle = animationStyle;
	}

	public void showDialog(View view) {
		createDialog(view);
		show();
	}

	public void showDialog(int layoutResID) {
		createDialog(layoutResID);
		show();
	}

	public void createDialog(View view) {
		setContentView(view);
		initDialog();
	}

	public void createDialog(int layoutResID) {
		setContentView(layoutResID);
		initDialog();
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
			params.dimAmount = 0.7f; // 该变量指示后面的窗口变暗的程度, 1.0表示完全不透明，0.0表示透明。
			window.setAttributes(params);
			window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		}
	}
}

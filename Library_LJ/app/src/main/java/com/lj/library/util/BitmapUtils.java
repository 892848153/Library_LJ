package com.lj.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {

	/**
	 * 根据资源的id返回{@link Bitmap}对象.
	 * 
	 * @param context
	 * @param drawId
	 * @return 解析成功返回{@link Bitmap}对象，否则返回null.
	 */
	public static Bitmap getBitmap(Context context, int drawId) {
		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, drawId);
		return bmp;
	}

	/**
	 * Returns the bitmap used by this drawable to render. May be null.
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			LogUtil.d(new Object(), "回收图片资源");
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
	}
}

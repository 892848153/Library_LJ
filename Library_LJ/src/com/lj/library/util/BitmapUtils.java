package com.lj.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class BitmapUtils {

	public static Bitmap getBitmap(Context context, int drawId) {
		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, drawId);
		return bmp;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		BitmapDrawable bd = (BitmapDrawable) drawable;
		return bd.getBitmap();
	}

	public static void recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
	}
}

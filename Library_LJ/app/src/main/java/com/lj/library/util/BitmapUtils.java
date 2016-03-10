package com.lj.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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

    /**
     * 将图片转换成圆形图.
     *
     * @param source
     * @param diameter 直径
     * @return
     */
    public static Bitmap createCircleImage(Bitmap source, int diameter) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap circleBmp = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(circleBmp);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(diameter / 2, diameter / 2, diameter / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return circleBmp;
    }

    /**
     * 将图片转换成圆角图.
     *
     * @param source
     * @param width
     * @param height
     * @param cornerRadius 角半径
     * @return
     */
    public static Bitmap createRoundImage(Bitmap source, int width, int height, float cornerRadius) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap roundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(roundBmp);
        /**
         * 首先绘制圆角矩形
         */
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return roundBmp;
    }
}

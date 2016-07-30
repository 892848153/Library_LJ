package com.lj.library.widget.renderperform;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by jie.liu on 16/2/26.
 */
public class CustomFrameLayout extends FrameLayout {

    private static final String TAG = CustomFrameLayout.class.getSimpleName();


    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure start");
        long start = System.nanoTime();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure end:" + (System.nanoTime() - start));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e(TAG, "onLayout start");
        long start = System.nanoTime();
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout end:" + (System.nanoTime() - start));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw start");
        long start = System.nanoTime();
        super.onDraw(canvas);
        Log.e(TAG, "onDraw end:" + (System.nanoTime() - start));
    }
}

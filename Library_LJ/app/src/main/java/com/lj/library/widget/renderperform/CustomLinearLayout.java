package com.lj.library.widget.renderperform;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * Created by jie.liu on 16/2/26.
 */
public class CustomLinearLayout extends LinearLayout {

    private static final String TAG = CustomLinearLayout.class.getSimpleName();


    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
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

package com.lj.library.widget.renderperform;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by liujie_gyh on 16/9/20.
 */
public class CustomListView extends ListView {

    private static final String TAG = CustomListView.class.getSimpleName();

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure start");
        long start = System.nanoTime();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure end:" + (System.nanoTime() - start));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d(TAG, "onLayout start");
        long start = System.nanoTime();
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout end:" + (System.nanoTime() - start));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw start");
        long start = System.nanoTime();
        super.onDraw(canvas);
        Log.d(TAG, "onDraw end:" + (System.nanoTime() - start));
    }


}

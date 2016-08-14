package com.lj.library.widget.viewpager.inner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.lj.library.widget.viewpager.customduration.CustomDurationViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujie_gyh on 16/8/11.
 */
public class InnerCustomDurationViewPager extends CustomDurationViewPager{

    private List<ViewGroup> mParentViews;

    public InnerCustomDurationViewPager(Context context) {
        super(context);
        init();
    }

    public InnerCustomDurationViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (mParentViews == null) {
            mParentViews = new ArrayList<ViewGroup>();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean resume = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setParentScrollable(false);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setParentScrollable(true);
                break;
        }
        return resume;
    }

    /**
     * 增加包含它的可滑动的View，这样可以使滑动动作不冲突.
     *
     * @param parentView
     */
    public void addParentScrollView(ViewGroup parentView) {
        if (!mParentViews.contains(parentView)) {
            mParentViews.add(parentView);
        }
    }

    /**
     *
     * 是否把滚动事件交给父Scrollview.
     *
     * @param flag
     */
    private void setParentScrollable(boolean flag) {
        for (ViewGroup parentView : mParentViews) {
            parentView.requestDisallowInterceptTouchEvent(!flag);
        }
    }
}

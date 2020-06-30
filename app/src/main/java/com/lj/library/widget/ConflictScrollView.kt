package com.lj.library.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * Created by liujie_gyh on 2020/6/29.
 */
private const val TAG = "HorizontalScrollViewEx"

class HorizontalScrollViewEx : ScrollView {

    private var mLastXIntercept = 0.0f
    private var mLastYIntercept = 0.0f


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        val x = ev.x
        val y = ev.y

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (needIntercept()) {
                    // do something
                    val i = 0
                    intercepted = true
                } else {
                    intercepted = false
                }
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
            MotionEvent.ACTION_CANCEL -> {
                intercepted = false
            }
        }

        mLastXIntercept = x
        mLastYIntercept = y

        return intercepted
    }

    private fun needIntercept(): Boolean {
        return false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}
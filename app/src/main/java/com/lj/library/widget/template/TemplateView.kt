package com.lj.library.widget.template

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.lj.library.R

/**
 * Created by liujie_gyh on 2020/6/26.
 */
class TemplateView: View {

    private val DEFAULT_WIDTH = 100
    private val DEFAULT_HEIGHT = 100

    private var mWidthWithoutPadding: Int = -1
    private var mHeightWithoutPadding: Int = -1

    private var mColorAttr: Int = Color.BLACK

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TemplateView)
        mColorAttr = typedArray.getColor(R.styleable.TemplateView_tv_color, Color.BLACK)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        // mode == AT_MOST,则表明layoutParams中设置了wrap_content, 此时给一个默认值或者根据内容给出一个需要的大小
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT)
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, heightSpecSize)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, DEFAULT_HEIGHT)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 需要在此考虑padding，否则设置padding无效。margin属性parent会处理，这里无需关心
        if (mWidthWithoutPadding == -1) mWidthWithoutPadding = width - paddingLeft - paddingRight
        if (mHeightWithoutPadding == -1) mHeightWithoutPadding = height - paddingTop - paddingBottom



    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 做一些回收资源，停止动画之类的收尾工作
    }
}
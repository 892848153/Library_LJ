package com.lj.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.lj.library.R;
import com.lj.library.util.Logger;

/**
 * 自定义控件的参照
 * Created by liujie_gyh on 2018/5/13.
 * @author LJ.Liu
 */
public class CustomView extends ViewGroup {

    private int mAttrInt;

    private boolean mAttrBoolean;

    private float mAttrFloat;

    private int mAttrColor;

    private int mAttrDimension;

    private int mAttrReference;

    private String mAttrString;
    private String mAttrAndroidText;

    private int mAttrEnum;

    private Paint mPaint;

    private int[] mAttrs = {R.attr.global_attr_one, R.attr.global_attr_two};
    public static final int ATTR_INDEX_ONE = 0;
    public static final int ATTR_INDEX_TWO = 1;




    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CustomView);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int index = typedArray.getIndex(i);
            switch (index) {
                case R.styleable.CustomView_custom_integer:
                    mAttrInt = typedArray.getInteger(R.styleable.CustomView_custom_integer, -1);
                    break;
                case R.styleable.CustomView_custom_boolean:
                    mAttrBoolean = typedArray.getBoolean(R.styleable.CustomView_custom_boolean, false);
                    break;
                case R.styleable.CustomView_custom_color:
                    mAttrColor = typedArray.getColor(R.styleable.CustomView_custom_color, 0xFFFFFFFF);
                    break;
                case R.styleable.CustomView_custom_dimension:
                    mAttrDimension = typedArray.getDimensionPixelOffset(R.styleable.CustomView_custom_dimension, 0);
                    break;
                case R.styleable.CustomView_custom_float:
                    mAttrFloat = typedArray.getFloat(R.styleable.CustomView_custom_float, 0.0f);
                    break;
                case R.styleable.CustomView_custom_reference:
                    mAttrReference = typedArray.getResourceId(R.styleable.CustomView_custom_reference, 0);
                    break;
                case R.styleable.CustomView_custom_fraction:
//                    typedArray.getFraction(R.styleable.CustomView_custom_fraction, )
                    break;
                case R.styleable.CustomView_custom_string:
                    mAttrString = typedArray.getString(R.styleable.CustomView_custom_string);
                    break;
                case R.styleable.CustomView_android_text:
                    mAttrAndroidText = typedArray.getString(R.styleable.CustomView_android_text);
                case R.styleable.CustomView_custom_enum:
                    mAttrEnum = typedArray.getInt(R.styleable.CustomView_custom_enum, 0);
                    break;
                case R.styleable.CustomView_custom_flag:

                    break;
                default:
            }
        }


        Logger.i(String.format("mAttrInt:%1$d, mAttrBoolean:%2$b, mAttrColor:%3$d, mAttrDimension:%4$d, " +
                "mAttrFloat: %5$f, mAttrReference:%6$d, mAttrString:%7$s, mAttrAndroidText:%8$s, mAttrEnum:%9$d", mAttrInt, mAttrBoolean,mAttrColor,
                mAttrDimension,mAttrFloat, mAttrReference, mAttrString, mAttrAndroidText, mAttrEnum));

        typedArray.recycle();



        TypedArray globalAttrsTypedArray = context.obtainStyledAttributes(attributeSet, mAttrs);
        String attrOne = globalAttrsTypedArray.getString(ATTR_INDEX_ONE);
        int attrTwo = globalAttrsTypedArray.getInt(ATTR_INDEX_TWO, 0);

        Logger.i(String.format("global attr without styleable, attrOne:%1$s, attrTwo:%2$d", attrOne, attrTwo));
        globalAttrsTypedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(38);
        mPaint.setTextAlign(Paint.Align.LEFT);

    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        // 剧中画字  https://blog.csdn.net/zly921112/article/details/50401976
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        String text = "Custom View";
        //测量文字宽度
        float textWidth = mPaint.measureText(text);
        //计算文字基线Y坐标。控件Y中心点加上一段距离就是BaseLineY了，这段距离计算方式为：字体Y中心点减去descent，
        // 这样就保证了字的Y中心点和控件的Y中心点重合。
        float textBaseLineY = getHeight()/2 + (fm.descent-fm.ascent)/2  - fm.descent;
        // textX根据paint的setTextAlign方法的对其方式来决定.
        float textX = (getWidth() - textWidth) / 2;
        canvas.drawText(text, textX, textBaseLineY, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        // TODO 多指触控
        return super.onInterceptTouchEvent(ev);
    }
}

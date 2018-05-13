package com.lj.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.lj.library.R;
import com.lj.library.util.Logger;

/**
 * Created by liujie_gyh on 2018/5/13.
 */
public class CustomView extends ViewGroup {

    private int mAttrInt;

    private boolean mAttrBoolean;

    private float mAttrFloat;

    private int mAttrColor;

    private int mAttrDimension;

    private int mAttrReference;

    private String mAttrString;

    private int mAttrEnum;


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
                    mAttrString = typedArray.getString(R.styleable.CustomView_android_text);
                    break;
                case R.styleable.CustomView_custom_enum:
                    mAttrEnum = typedArray.getInt(R.styleable.CustomView_custom_enum, 0);
                    break;
                case R.styleable.CustomView_custom_flag:

                    break;
                default:
            }
        }


        Logger.i("mAttrInt:%1$d, mAttrBoolean:%2$b, mAttrColor:%3$d, mAttrDimension:%4$d, " +
                "mAttrFloat: %5$f, mAttrReference:%6$d, mAttrString:%7$s, mAttrEnum:%8$d");

        typedArray.recycle();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}

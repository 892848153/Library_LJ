package com.lj.library.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.lj.library.bean.Point;
import com.lj.library.util.LogUtil;

/**
 * Created by liujie_gyh on 15/10/18.
 */
public class AnimatorView extends View {

    public static final float RADIUS = 50f;

    private Point currentPoint;

    private String color;

    private Paint mPaint;

    public AnimatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentPoint == null) {
            currentPoint = new Point(RADIUS, RADIUS);
            drawCircle(canvas);
            startAnimation();
        } else {
            drawCircle(canvas);
        }
    }

    private void drawCircle(Canvas canvas) {
        float x = currentPoint.getX();
        float y = currentPoint.getY();
        canvas.drawCircle(x, y, RADIUS, mPaint);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void startAnimation() {
        Point startPoint = new Point(RADIUS, RADIUS);
        Point endPoint = new Point(getWidth() - RADIUS, getHeight() - RADIUS);
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentPoint = null;
                invalidate();
            }
        });
        ObjectAnimator anim2 = ObjectAnimator.ofObject(this, "color", new ColorEvaluator(),
                "#0000FF", "#FF0000");
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(anim).with(anim2);
        animSet.setDuration(5000);
        animSet.start();
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        LogUtil.i(this, "parseColor:" + color);
        mPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class PointEvaluator implements TypeEvaluator {

        private Point mPoint = new Point(0, 0);

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            float x = startPoint.getX() + fraction * (endPoint.getX() - startPoint.getX());
            float y = startPoint.getY() + fraction * (endPoint.getY() - startPoint.getY());
            mPoint.setX(x);
            mPoint.setY(y);
            return mPoint;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private class ColorEvaluator implements TypeEvaluator {

        private int mCurrentRed = -1;

        private int mCurrentGreen = -1;

        private int mCurrentBlue = -1;

        private StringBuilder mColorHexValueSb = new StringBuilder(7);

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            String startColor = (String) startValue;
            String endColor = (String) endValue;
            int startRed = Integer.parseInt(startColor.substring(1, 3), 16);
            int startGreen = Integer.parseInt(startColor.substring(3, 5), 16);
            int startBlue = Integer.parseInt(startColor.substring(5, 7), 16);
            int endRed = Integer.parseInt(endColor.substring(1, 3), 16);
            int endGreen = Integer.parseInt(endColor.substring(3, 5), 16);
            int endBlue = Integer.parseInt(endColor.substring(5, 7), 16);
            // 初始化颜色的值
            if (mCurrentRed == -1) {
                mCurrentRed = startRed;
            }
            if (mCurrentGreen == -1) {
                mCurrentGreen = startGreen;
            }
            if (mCurrentBlue == -1) {
                mCurrentBlue = startBlue;
            }
            // 计算初始颜色和结束颜色之间的差值
            int redDiff = Math.abs(startRed - endRed);
            int greenDiff = Math.abs(startGreen - endGreen);
            int blueDiff = Math.abs(startBlue - endBlue);
            int colorDiff = redDiff + greenDiff + blueDiff;
            if (mCurrentRed != endRed) {
                mCurrentRed = getCurrentColor(startRed, endRed, colorDiff, 0, fraction);
            } else if (mCurrentGreen != endGreen) {
                mCurrentGreen = getCurrentColor(startGreen, endGreen, colorDiff, redDiff, fraction);
            } else if (mCurrentBlue != endBlue) {
                mCurrentBlue = getCurrentColor(startBlue, endBlue, colorDiff, redDiff + greenDiff, fraction);
            }
            // 将计算出的当前颜色的值组装返回
            mColorHexValueSb.delete(0, mColorHexValueSb.length()).append('#');
            appendColorHexString(mCurrentRed, mColorHexValueSb);
            appendColorHexString(mCurrentGreen, mColorHexValueSb);
            appendColorHexString(mCurrentBlue, mColorHexValueSb);
            return mColorHexValueSb.toString();
        }

        /**
         * 根据fraction值来计算当前的颜色。
         */
        private int getCurrentColor(int startColor, int endColor, int colorDiff,
                                    int offset, float fraction) {
            int currentColor;
            if (startColor > endColor) {
                currentColor = (int) (startColor - (fraction * colorDiff - offset));
                if (currentColor < endColor) {
                    currentColor = endColor;
                }
            } else {
                currentColor = (int) (startColor + (fraction * colorDiff - offset));
                if (currentColor > endColor) {
                    currentColor = endColor;
                }
            }
            return currentColor;
        }

        /**
         * 将10进制颜色值转换成16进制。
         */
        private void appendColorHexString(int value, StringBuilder sb) {
            String hexString = Integer.toHexString(value);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }

    }

}

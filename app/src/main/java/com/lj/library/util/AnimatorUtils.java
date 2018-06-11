package com.lj.library.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.view.View;

/**
 * @author LJ.Liu
 * @date 2018/6/11.
 */
public class AnimatorUtils {

    public static ObjectAnimator getAlphaAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.ALPHA.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getScaleAndAlphaAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, values);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, values);
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat(View.ALPHA, values);
        return getAnimator(target, duration, interpolator, scaleXHolder, scaleYHolder, alphaHolder);
    }

    public static ObjectAnimator getScaleAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, values);
        PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, values);
        return getAnimator(target, duration, interpolator, scaleXHolder, scaleYHolder);
    }

    /**
     * scale属性比较特殊,不管是scaleX还是scaleY,只要有一个是0即此View缩放到大小为0,则此View的触摸区域是View原来的区域.
     * @param target
     * @param duration
     * @param interpolator
     * @param values 取值[0F, 1.0F]
     * @return
     */
    public static ObjectAnimator getScaleXAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.SCALE_X.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getScaleYAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.SCALE_Y.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getRotationAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.ROTATION.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getRotationXAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.ROTATION_X.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getRotationYAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.ROTATION_Y.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getTranslationXAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.TRANSLATION_X.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getTranslationYAnimator(View target, long duration, TimeInterpolator interpolator, float... values) {
        return getAnimator(target, View.TRANSLATION_Y.getName(), duration, interpolator, values);
    }

    public static ObjectAnimator getAnimator(View target, String propertyName, long duration, TimeInterpolator interpolator, float... values) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target, propertyName, values);
        configObjectAnimator(objectAnimator, interpolator, duration);
        return objectAnimator;
    }

    public static ObjectAnimator getAnimator(View target, long duration, TimeInterpolator interpolator, PropertyValuesHolder... propertyValuesHolders) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(target, propertyValuesHolders);
        configObjectAnimator(objectAnimator, interpolator, duration);
        return objectAnimator;
    }

    private static void configObjectAnimator(ObjectAnimator objectAnimator, TimeInterpolator interpolator, long duration) {
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(interpolator);
    }
}

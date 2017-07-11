package com.lj.library.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;

import java.lang.reflect.Field;

/**
 *
 * Created by ocean on 2017/5/26.
 */
public class FontSwitcherUtils {

    private static final String TAG = "FontSwitcherUtils";

    public static final String FONT_LIGHT = "fonts/SF-UI-Text-Light.ttf";

    /**
     * 要被替换掉的字体类型在{@link Typeface}中的成员变量名
     */
    private static final String FIELD_NAME_IN_TYPEFACE_REPLACED = "SERIF";

    public static final void switchFont(Context context) {
        switchFont(context, FONT_LIGHT);
    }

    /**
     * 系统主题里面必须设置字体类型为"serif".
     *
     * @param context
     * @param targetFont 字体类型.取值:{@link FontSwitcherUtils#FONT_LIGHT}
     */
    public static final void switchFont(Context context, String targetFont) {
        if (context == null) {
            LogUtil.e(TAG, "switchFont(context, targetfont) find context is null");
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(targetFont)) {
            LogUtil.e(TAG, "switchFont(context, targetfont) find targetFont is empty");
            throw new IllegalArgumentException("targetFont is empty");
        }

        try {
            Field field = Typeface.class.getDeclaredField(FIELD_NAME_IN_TYPEFACE_REPLACED);
            field.setAccessible(true);
            field.set(null, Typeface.createFromAsset(context.getAssets(), targetFont));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

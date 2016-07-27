package com.lj.library.util;

import android.content.Context;

/**
 * 跟Ui有关的工具类.
 * Created by jie.liu on 16/7/27.
 */
public class UIUtils {

    /**
     * 获取状态栏高度.
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

}

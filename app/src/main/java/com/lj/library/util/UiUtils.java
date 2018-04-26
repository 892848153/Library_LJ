package com.lj.library.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 跟Ui有关的工具类.
 * Created by jie.liu on 16/7/27.
 */
public class UiUtils {

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

    /**
     * 父布局中是否包含这个子布局.
     *
     * @param layout 子布局
     * @param parentLayout 父布局
     * @return
     */
    public static boolean parentLayoutContains(ViewGroup parentLayout, View layout) {
        int childCount = parentLayout.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                if (parentLayout.getChildAt(i) == layout) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static ViewGroup getAndroidContentView(Activity activity) {
        return (ViewGroup) activity.findViewById(android.R.id.content);
    }

}

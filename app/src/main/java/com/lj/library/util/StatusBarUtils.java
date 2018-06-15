package com.lj.library.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lj.library.R;

/**
 * Android 4.4采用半透明标题栏方式把布局顶到状态栏底下，5.0开始不用设置半透明状态栏也可以把布局顶到状态栏底下.
 * 不管是采用半透明状态栏的方式还是别的方式，只要把布局顶到状态栏底下，那么android:windowSoftInputMode="adjustResize"
 * 将失效。只有在根布局设置android:fitsSystemWindows="true"才可以让上面的属性生效
 * 不过设置了android:fitsSystemWindows="true"那么此View会给自己增加一个status bar高度的paddingTop
 * 这样布局又不能顶到状态栏底下了，这是个矛盾.
 *
 * @author LJ.Liu
 * @date 2018/6/14.
 */
public class StatusBarUtils {

    private static final int STATUS_BAR_HOLDER_ID = R.id.status_bar_holder_id;

    /**
     * Android 4.4采用半透明状态栏方式, 5.0开始直接把布局顶到状态栏底下.
     * 采用{@link #overflowStatusBar(Activity, boolean)} + {@link #setFitsSystemWindows(Activity, boolean)}，
     * 的方式实现沉浸式状态栏的话, 状态栏的背景显示的是整体布局的背景。
     * @param activity
     * @param overflow
     */
    public static void overflowStatusBar(Activity activity, boolean overflow) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overflowStatusBarSinceLollipop(activity, overflow);
        } else {
            translucentStatusBarSinceKitkat(activity, overflow);
        }
    }

    /**
     * Android 4.4采用半透明状态栏方式, 5.0开始直接把布局顶到状态栏底下.
     * 采用这种方式实现沉浸式的话，可以指定状态栏的底色，
     * 不过android:windowSoftInputMode="adjustResize"将失效
     *
     * @param activity
     * @param overflow
     * @param colorId
     */
    public static void overflowStatusBar(Activity activity, boolean overflow, int colorId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overflowStatusBarSinceLollipop(activity, overflow);
        } else {
            translucentStatusBarSinceKitkat(activity, overflow);
        }

        addStatusBarHolderWithColor(activity, colorId);
    }

    private static void overflowStatusBarSinceLollipop(Activity activity, boolean overflow) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        int option = decorView.getSystemUiVisibility();
        if (overflow) {
            option = option | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        } else {
            option = option & ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN & ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        }
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private static void translucentStatusBarSinceKitkat(Activity activity, boolean translucent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (translucent) {
            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        } else {
            attributes.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        window.setAttributes(attributes);
    }

    /**
     * 设置为true时，系统会给布局增加一个状态栏高度的top padding, 这样布局背景就显示在系统的status bar下面
     * @param activity
     * @param fitSystemWindows
     */
    public static void setFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        View contentView = UiUtils.getAndroidContentView(activity).getChildAt(0);
        contentView.setFitsSystemWindows(fitSystemWindows);
    }

    /**
     * 添加状态栏占位视图
     *
     * @param activity
     */
    public static void addStatusBarHolderWithColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        removeStatusBarHolderIfExist(activity);

        ViewGroup androidContentView = UiUtils.getAndroidContentView(activity);
        View rootView = androidContentView.getChildAt(0);
        int statusBarHeight = UiUtils.getStatusBarHeight(activity);

        setViewTopMargin(rootView, statusBarHeight);

        View statusBarHolder = new View(activity);
        statusBarHolder.setId(STATUS_BAR_HOLDER_ID);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        statusBarHolder.setBackgroundResource(colorId);
        androidContentView.addView(statusBarHolder, 0, lp);
    }

    /**
     * 图片需要顶上状态栏时使用此方法.
     */
    public static void removeStatusBarHolderIfExist(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        ViewGroup androidContentView = UiUtils.getAndroidContentView(activity);
        View rootView = androidContentView.getChildAt(0);
        boolean rootViewIsStatusBarHolder = rootView != null && rootView.getId() == STATUS_BAR_HOLDER_ID;
        if (rootViewIsStatusBarHolder) {
            androidContentView.removeViewAt(0);

            rootView = androidContentView.getChildAt(0);
            setViewTopMargin(rootView, 0);
        }
    }

    private static void setViewTopMargin(final View view, final int topMargin) {
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = topMargin;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 状态栏亮色模式，状态栏字体颜色为黑色.
     */
    public static void lightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(activity,false);
        }
    }

    /**
     * 状态栏暗色模式，状态栏字体颜色为白色.
     */
    public static void darkStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSystemBarTheme(activity,true);
        }
    }

    /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static final void setSystemBarTheme(Activity activity, final boolean isDark) {
        // Fetch the current flags.
        final int curFlags = activity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        activity.getWindow().getDecorView().setSystemUiVisibility(isDark ? (curFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (curFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));

    }
}

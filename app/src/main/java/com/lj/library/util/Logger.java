package com.lj.library.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.LogAdapter;
import com.orhanobut.logger.Printer;

/**
 * Created by liujie_gyh on 16/5/28.
 * @author LJ.Liu
 */
public class Logger {

    private Logger() {
    }

    public static void printer(@NonNull Printer printer) {
        com.orhanobut.logger.Logger.printer(printer);
    }

    public static void addLogAdapter(@NonNull LogAdapter adapter) {
        com.orhanobut.logger.Logger.addLogAdapter(adapter);
    }

    public static void clearLogAdapters() {
        com.orhanobut.logger.Logger.clearLogAdapters();
    }

    public static Printer t(@Nullable String tag) {
        return com.orhanobut.logger.Logger.t(tag);
    }

    public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        com.orhanobut.logger.Logger.log(priority, tag, message, throwable);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.d(message, args);
    }

    public static void d(@Nullable Object object) {
        com.orhanobut.logger.Logger.d(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.e((Throwable)null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.e(throwable, message, args);
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.v(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.w(message, args);
    }

    public static void wtf(@NonNull String message, @Nullable Object... args) {
        com.orhanobut.logger.Logger.wtf(message, args);
    }

    public static void json(@Nullable String json) {
        com.orhanobut.logger.Logger.json(json);
    }

    public static void xml(@Nullable String xml) {
        com.orhanobut.logger.Logger.xml(xml);
    }

}

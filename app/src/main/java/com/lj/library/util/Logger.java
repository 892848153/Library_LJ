package com.lj.library.util;

import com.lj.library.BuildConfig;
import com.orhanobut.logger.Printer;
import com.orhanobut.logger.Settings;

/**
 * Created by liujie_gyh on 16/5/28.
 */
public class Logger {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    //no instance
    private Logger() {
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static Settings init() {
        return com.orhanobut.logger.Logger.init();
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    public static Settings init(String tag) {
        return com.orhanobut.logger.Logger.init(tag);
    }

    public static Printer t(String tag) {
        return com.orhanobut.logger.Logger.t(tag);
    }

    public static Printer t(int methodCount) {
        return com.orhanobut.logger.Logger.t(methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return com.orhanobut.logger.Logger.t(tag, methodCount);
    }

    public static void d(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.d(message, args);
    }

    public static void e(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.e(message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.i(message, args);
    }

    public static void v(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.v(message, args);
    }

    public static void w(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        if (DEBUG) com.orhanobut.logger.Logger.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        if (DEBUG) com.orhanobut.logger.Logger.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        if (DEBUG) com.orhanobut.logger.Logger.xml(xml);
    }

}

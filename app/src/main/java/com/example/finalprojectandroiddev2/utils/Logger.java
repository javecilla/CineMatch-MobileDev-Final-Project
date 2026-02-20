package com.example.finalprojectandroiddev2.utils;

import android.util.Log;

import com.example.finalprojectandroiddev2.BuildConfig;

/**
 * Centralized logging utility. Filters verbose/debug output in release builds.
 */
public final class Logger {

    private Logger() {}

    private static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static void v(String tag, String msg) {
        if (isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable tr) {
        if (isDebug()) {
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (isDebug()) {
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }
}

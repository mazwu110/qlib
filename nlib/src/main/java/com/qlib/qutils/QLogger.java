package com.qlib.qutils;

import com.qlib.qremote.QApp;

import android.util.Log;

public class QLogger {


    private QLogger() {
    }

    public static void d(String tag, String desc) {
        if (QApp.DEBUG)
            Log.d(tag, desc);
    }

    public static void d(String tag, String desc, Throwable tr) {
        if (QApp.DEBUG)
            Log.d(tag, desc, tr);
    }

    public static void v(String tag, String desc) {
        if (QApp.DEBUG)
            Log.v(tag, desc);
    }

    public static void v(String tag, String desc, Throwable tr) {
        if (QApp.DEBUG)
            Log.v(tag, desc);
    }

    public static void w(String tag, String desc) {
        if (QApp.DEBUG)
            Log.w(tag, desc);
    }

    public static void w(String tag, Throwable ioe) {
        if (QApp.DEBUG)
            Log.w(tag, ioe);
    }

    public static void w(String tag, String desc, Throwable e) {
        if (QApp.DEBUG)
            Log.w(tag, desc, e);
    }

    public static void i(String tag, String desc) {
        if (QApp.DEBUG)
            Log.i(tag, desc);
    }

    public static void i(String tag, String desc, Throwable tr) {
        if (QApp.DEBUG)
            Log.i(tag, desc, tr);
    }

    public static void e(String tag, String desc) {
        if (QApp.DEBUG)
            Log.e(tag, desc);
    }

    public static void e(String tag, String desc, Throwable tr) {
        if (QApp.DEBUG)
            Log.e(tag, desc, tr);
    }
}

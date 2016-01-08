package com.afilimonov.gitbrowser.utils;

import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: afilimonov
 * Date: 04.12.2015
 * Time: 16:04
 */
public class Logger {

    public static final String TAG = "gitBrowserLog";

    public static void d(String text) {
        Log.d(TAG, text);
    }

    public static void e(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
    }
}

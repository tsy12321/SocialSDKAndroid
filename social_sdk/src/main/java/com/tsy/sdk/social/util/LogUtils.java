package com.tsy.sdk.social.util;

import android.util.Log;

/**
 * Created by tsy on 16/8/4.
 */
public class LogUtils {

    public static final String TAG = "socialsdk";

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void i(String module, String msg) {
        Log.i(TAG, "[" + module + "]" + msg);
    }

    public static void e(String module, String msg) {
        Log.e(TAG, "[" + module + "]" + msg);
    }

    public static void d(String module, String msg) {
        Log.d(TAG, "[" + module + "]" + msg);
    }

    public static void v(String module, String msg) {
        Log.v(TAG, "[" + module + "]" + msg);
    }

    public static void w(String module, String msg) {
        Log.w(TAG, "[" + module + "]" + msg);
    }
}

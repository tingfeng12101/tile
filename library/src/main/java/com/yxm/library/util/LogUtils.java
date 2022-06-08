package com.yxm.library.util;

import android.util.Log;

/**
 * Log统一管理类
 */
public class LogUtils {

    private LogUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static String TAG = "tile";

    // 下面四个是默认tag的函数
    public static void i(Object msg) {
        if (isDebug)
            Log.i(TAG, msg == null ? "null" : msg.toString());
    }

    public static void d(Object msg) {
        if (isDebug)
            Log.d(TAG, msg == null ? "null" : msg.toString());
    }

    public static void w(Object msg) {
        if (isDebug)
            Log.w(TAG, msg == null ? "null" : msg.toString());
    }

    public static void e(Object msg) {
        if (isDebug)
            Log.e(TAG, msg == null ? "null" : msg.toString());
    }

    public static void v(Object msg) {
        if (isDebug)
            Log.v(TAG, msg == null ? "null" : msg.toString());
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, Object msg) {
        if (isDebug)
            Log.i(tag, msg == null ? "null" : msg.toString());
    }

    public static void d(String tag, Object msg) {
        if (isDebug)
            Log.i(tag, msg == null ? "null" : msg.toString());
    }

    public static void w(String tag, Object msg) {
        if (isDebug)
            Log.w(tag, msg == null ? "null" : msg.toString());
    }

    public static void e(String tag, Object msg) {
        if (isDebug)
            Log.i(tag, msg == null ? "null" : msg.toString());
    }

    public static void v(String tag, Object msg) {
        if (isDebug)
            Log.i(tag, msg == null ? "null" : msg.toString());
    }
}
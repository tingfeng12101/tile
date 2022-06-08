package com.yxm.library.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences存储数据方式工具类
 */
public class SharedPrefsUtil {
    public final static String SETTING = "Setting_CD";

    public static void putValue(Context context, String key, int value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putInt(key, value);
        sp.apply();
    }

    public static void putValue(Context context, String key, boolean value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putBoolean(key, value);
        sp.apply();
    }

    public static void putValue(Context context, String key, String value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putString(key, value);
        sp.apply();
    }

    public static void putValue(Context context, String key, Long value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.putLong(key, value);
        sp.apply();
    }

    public static void removeValue(Context context, String key) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
                .edit();
        sp.remove(key);
        sp.apply();
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static boolean getValue(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static String getValue(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static Long getValue(Context context, String key, Long defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE);
        return sp.getLong(key, defValue);
    }
}

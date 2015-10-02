package com.lovely3x.common.utils;

import android.util.Log;

/**
 * Created by lovely3x on 15-8-16.
 * 日志工具,封装了下系统的日志工具,便于管理
 */
public class ALog {

    public static final boolean DEBUG = true;

    private static final String DEFAULT_MSG = "ALog";


    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, String msg, Exception e) {
        if (DEBUG) {
            if (e == null) {
                e(tag, msg);
            } else {
                Log.e(tag == null ? "null" : tag, msg == null ? "null" : msg, e);
            }
        }
    }

    public static void e(String tag, Exception e) {
        if (DEBUG) {
            if (e == null) {
                e(tag, DEFAULT_MSG);
            } else {
                Log.e(tag == null ? "null" : tag, DEFAULT_MSG, e);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void w(String tag, String msg, Exception e) {
        if (DEBUG) {
            if (e == null) {
                w(tag, msg);
            } else {
                Log.w(tag == null ? "null" : tag, msg == null ? "null" : msg, e);
            }
        }
    }

    public static void w(String tag, Exception e) {
        if (DEBUG) {
            if (e == null) {
                w(tag, DEFAULT_MSG);
            } else {
                Log.w(tag == null ? "null" : tag, DEFAULT_MSG, e);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag == null ? "null" : tag, msg == null ? "null" : msg);
        }
    }

    public static void e(String tag, Object... params) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                Object s = params[i];
                sb.append(s == null ? "null" : s.toString());
                if (i + 1 != params.length) sb.append(",");
            }
            Log.e(tag, sb.toString());
        }
    }

}

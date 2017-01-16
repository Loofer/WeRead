package org.loofer.framework.utils;

import android.util.Log;

public class LogUtils {

    protected static final String TAG = "WeRead";
    public static boolean DEBUG = true;

    private LogUtils() {
    }

    public static void d(String message) {
        if (DEBUG)
            Log.d(TAG, buildMessage(message));
    }

    public static void e(String message) {
        if (DEBUG)
            Log.e(TAG, buildMessage(message));
    }

    public static void i(String message) {
        if (DEBUG)
            Log.i(TAG, buildMessage(message));
    }

    public static void v(String message) {
        if (DEBUG)
            Log.v(TAG, buildMessage(message));
    }

    public static void w(String message) {
        if (DEBUG)
            Log.w(TAG, buildMessage(message));
    }

    public static void wtf(String message) {
        if (DEBUG)
            Log.wtf(TAG, buildMessage(message));
    }

    public static void println(String message) {
        if (DEBUG)
            Log.println(Log.INFO, TAG, message);
    }

    private static String buildMessage(String rawMessage) {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String fullClassName = caller.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return className + "." + caller.getMethodName() + "(): " + rawMessage;
    }


}

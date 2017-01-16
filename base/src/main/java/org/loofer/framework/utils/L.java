package org.loofer.framework.utils;

import com.orhanobut.logger.Logger;

public class L {

    protected static final String TAG = "WeRead";
    public static boolean DEBUG = true;

    private L() {
    }

    /**
     * initialize the logger.
     */
    public static void init() {
        Logger.init(TAG);
    }


    public static void d(String message) {
        if (DEBUG)
            Logger.d(message);
    }

    public static void e(String message) {
        if (DEBUG)
            Logger.e(message);
    }

    public static void i(String message) {
        if (DEBUG)
            Logger.i(message);
//            Logger.i(buildMessage(message));
    }

    public static void v(String message) {
        if (DEBUG)
            Logger.v(message);
//            Log.v(TAG, buildMessage(message));
    }

    public static void w(String message) {
        if (DEBUG)
            Logger.w(message);
//            Log.w(TAG, buildMessage(message));
    }

    public static void wtf(String message) {
        if (DEBUG)
            Logger.wtf(message);
//            Log.wtf(TAG, buildMessage(message));
    }

    public static void println(String message) {
//        if (DEBUG)
//            Logger.
//            Log.println(Log.INFO, TAG, message);
    }

    private static String buildMessage(String rawMessage) {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String fullClassName = caller.getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        return className + "." + caller.getMethodName() + "(): " + rawMessage;
    }


}
